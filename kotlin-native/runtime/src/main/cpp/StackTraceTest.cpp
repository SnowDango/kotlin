/*
 * Copyright 2010-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "StackTrace.hpp"

#include <signal.h>

#include "gmock/gmock.h"
#include "gtest/gtest.h"

#include "Common.h"
#include "Porting.h"
#include "TestSupport.hpp"

#include <iostream>

using namespace kotlin;

namespace {

template <size_t Capacity = kDynamicCapacity>
NO_INLINE StackTrace<Capacity> GetStackTrace1(size_t skipFrames = 0) {
    return StackTrace<Capacity>::current(skipFrames);
}

template <size_t Capacity = kDynamicCapacity>
NO_INLINE StackTrace<Capacity> GetStackTrace2(size_t skipFrames = 0) {
    return GetStackTrace1<Capacity>(skipFrames);
}

template <size_t Capacity = kDynamicCapacity>
NO_INLINE StackTrace<Capacity> GetStackTrace3(size_t skipFrames = 0) {
    return GetStackTrace2<Capacity>(skipFrames);
}

// Disable optimizations for these functions to avoid inlining and tail recursion optimization.
template <size_t Capacity = kDynamicCapacity>
[[clang::optnone]] StackTrace<Capacity> GetDeepStackTrace(size_t depth) {
    if (depth <= 1) {
        return StackTrace<Capacity>::current();
    } else {
        return GetDeepStackTrace<Capacity>(depth - 1);
    }
}

NO_INLINE void AbortWithStackTrace(int) {
    PrintStackTraceStderr();
    konan::abort();
}

} // namespace

TEST(StackTraceTest, StackTrace) {
    auto stackTrace = GetStackTrace3();
    auto symbolicStackTrace = GetStackTraceStrings(stackTrace.data());
    ASSERT_GT(symbolicStackTrace.size(), 0ul);
    EXPECT_THAT(symbolicStackTrace[0], testing::HasSubstr("GetStackTrace1"));
    EXPECT_THAT(symbolicStackTrace[1], testing::HasSubstr("GetStackTrace2"));
}

TEST(StackTraceTest, StackTraceWithSkip) {
    constexpr int kSkip = 1;
    auto stackTrace = GetStackTrace3(kSkip);
    auto symbolicStackTrace = GetStackTraceStrings(stackTrace.data());
    ASSERT_GT(symbolicStackTrace.size(), 0ul);
    EXPECT_THAT(symbolicStackTrace[0], testing::HasSubstr("GetStackTrace2"));
    EXPECT_THAT(symbolicStackTrace[1], testing::HasSubstr("GetStackTrace3"));
}

TEST(StackTraceTest, StackAllocatedTrace) {
    auto stackTrace = GetStackTrace3<2>();
    auto symbolicStackTrace = GetStackTraceStrings(stackTrace.data());
    ASSERT_EQ(symbolicStackTrace.size(), 2ul);
    EXPECT_THAT(symbolicStackTrace[0], testing::HasSubstr("GetStackTrace1"));
    EXPECT_THAT(symbolicStackTrace[1], testing::HasSubstr("GetStackTrace2"));
}

TEST(StackTraceTest, StackAllocatedTraceWithSkip) {
    constexpr int kSkip = 1;
    auto stackTrace = GetStackTrace3<2>(kSkip);
    auto symbolicStackTrace = GetStackTraceStrings(stackTrace.data());
    ASSERT_EQ(symbolicStackTrace.size(), 2ul);
    EXPECT_THAT(symbolicStackTrace[0], testing::HasSubstr("GetStackTrace2"));
    EXPECT_THAT(symbolicStackTrace[1], testing::HasSubstr("GetStackTrace3"));
}

TEST(StackTraceTest, EmptyStackTrace) {
    constexpr size_t kSkip = 1000000;
    auto stackTrace = GetStackTrace1(kSkip);
    EXPECT_EQ(stackTrace.size(), 0ul);
    auto data = stackTrace.data();
    EXPECT_EQ(data.size(), 0ul);
    auto symbolicStackTrace = GetStackTraceStrings(data);
    EXPECT_EQ(symbolicStackTrace.size(), 0ul);
}

TEST(StackTraceTest, StackAllocatedEmptyTrace) {
    constexpr size_t kSkip = 1000000;
    auto stackTrace = GetStackTrace1<1>(kSkip);
    EXPECT_EQ(stackTrace.size(), 0ul);
    auto data = stackTrace.data();
    EXPECT_EQ(data.size(), 0ul);
    auto symbolicStackTrace = GetStackTraceStrings(data);
    EXPECT_EQ(symbolicStackTrace.size(), 0ul);
}

TEST(StackTraceTest, DeepStackTrace) {
    constexpr size_t knownStackDepth = 100;
    auto stackTrace = GetDeepStackTrace(knownStackDepth);
    auto symbolicStackTrace = GetStackTraceStrings(stackTrace.data());

    ASSERT_GT(symbolicStackTrace.size(), 0ul);
    EXPECT_THAT(symbolicStackTrace[0], testing::HasSubstr("GetDeepStackTrace"));

    // Note that for platforms where libc unwind is used (e.g. MacOS) the size of a collected trace is limited.
    // See kotlin::internal::GetCurrentStackTrace(size_t skipFrames) for details.
    EXPECT_GT(symbolicStackTrace.size(), knownStackDepth);
    EXPECT_THAT(symbolicStackTrace[knownStackDepth - 1], testing::HasSubstr("GetDeepStackTrace"));
}

TEST(StackTraceTest, StackAllocatedDeepTrace) {
    constexpr size_t knownStackDepth = 100;
    constexpr size_t capacity = 10;
    auto stackTrace = GetDeepStackTrace<capacity>(knownStackDepth);
    auto symbolicStackTrace = GetStackTraceStrings(stackTrace.data());

    ASSERT_GT(symbolicStackTrace.size(), 0ul);
    EXPECT_THAT(symbolicStackTrace[0], testing::HasSubstr("GetDeepStackTrace"));

    EXPECT_EQ(symbolicStackTrace.size(), capacity);
    EXPECT_THAT(symbolicStackTrace[capacity - 1], testing::HasSubstr("GetDeepStackTrace"));
}

TEST(StackTraceDeathTest, PrintStackTrace) {
    EXPECT_DEATH(
            { AbortWithStackTrace(0); },
            testing::AllOf(
                    testing::HasSubstr("AbortWithStackTrace"), testing::HasSubstr("StackTraceDeathTest_PrintStackTrace_Test"),
                    testing::Not(testing::HasSubstr("PrintStackTraceStderr"))));
}

TEST(StackTraceDeathTest, PrintStackTraceInSignalHandler) {
    EXPECT_DEATH(
            {
                signal(SIGINT, &AbortWithStackTrace);
                raise(SIGINT);
            },
            testing::AllOf(
                    testing::HasSubstr("AbortWithStackTrace"),
                    testing::HasSubstr("StackTraceDeathTest_PrintStackTraceInSignalHandler_Test"),
                    testing::Not(testing::HasSubstr("PrintStackTraceStderr"))));
}
