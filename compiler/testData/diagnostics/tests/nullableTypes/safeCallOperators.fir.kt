// SKIP_TXT
// !DIAGNOSTICS: -UNUSED_PARAMETER

class A(
    val l: MutableList<Int>,
    val ll: MutableList<MutableList<Int>>,
    var w: Int,
    val q: () -> Unit
)

operator fun <T> MutableList<in T>?.plusAssign(t: Any?) {}
operator fun <T> MutableList<T>?.set(t: Int, w: Any?) {}
operator fun <T> List<T>?.get(t: Int): T? = this?.get(t)
operator fun Int?.inc(): Int = 1
operator fun (() -> Unit)?.invoke() {}

fun foo(a: A?) {
    a?.l += 1
    a?.l[0]
    a?.l[0]++
    a?.l[0] = 1

    a?.ll[0][0]
    a?.ll[0][0]++
    a?.ll[0][0] = 1
    // No warning is reported because
    // 1. All kinds of green code with safe+call + invoke we identified fails with CCE if `a != null`, anyway
    // 2. In case of null value, the behavior is intended (no call performed)
    a?.q()
    a?.w++

    (a?.l) += 1
    (a?.l)[0]
    (a?.l)[0]++
    (a?.l)[0] = 1

    (a?.ll)[0][0]
    (a?.ll)[0][0]++
    (a?.ll)[0][0] = 1
    (a?.q)()
    (a?.w)++

    a?.l.plusAssign(1)
    a?.l.get(0)
    a?.l.get(0).inc()
    a?.l.set(0, 1)

    a?.ll.get(0).get(0)
    a?.ll.get(0).get(0).inc()
    a?.ll.get(0).set(0, 1)
    a?.q.invoke()
    a?.w.inc()

    if (a != null) {
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!> += 1
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>[0]
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>[0]++
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>[0] = 1

        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>ll<!>[0][0]
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>ll<!>[0][0]++
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>ll<!>[0][0] = 1
        // No warning is reported because
        // 1. All kinds of green code with safe+call + invoke we identified fails with CCE if `a != null`, anyway
        // 2. In case of null value, the behavior is intended (no call performed)
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>q()<!>
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>w<!>++

        (<!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>) += 1
        (<!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>)[0]
        (<!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>)[0]++
        (<!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>)[0] = 1

        (<!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>ll<!>)[0][0]
        (<!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>ll<!>)[0][0]++
        (<!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>ll<!>)[0][0] = 1
        (<!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>q<!>)()
        (<!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>w<!>)++

        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>.plusAssign(1)
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>.get(0)
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>.get(0).inc()
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>l<!>.set(0, 1)

        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>ll<!>.get(0).get(0)
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>ll<!>.get(0).get(0).inc()
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>ll<!>.get(0).set(0, 1)
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>q<!>.invoke()
        <!SAFE_CALL_WILL_CHANGE_NULLABILITY!>a<!UNNECESSARY_SAFE_CALL!>?.<!>w<!>.inc()
    }
}
