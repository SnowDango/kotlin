/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.light.classes.symbol.decompiled.service

import com.intellij.mock.MockApplication
import com.intellij.mock.MockProject
import com.intellij.openapi.Disposable
import org.jetbrains.kotlin.analysis.api.fir.utils.libraries.binary.LibraryFrontendApiTestConfiguratorService
import org.jetbrains.kotlin.analysis.api.impl.barebone.test.FrontendApiTestConfiguratorService
import org.jetbrains.kotlin.analysis.api.impl.base.test.utils.libraries.CompiledLibraryProvider
import org.jetbrains.kotlin.analysis.decompiled.light.classes.ClsJavaStubByVirtualFileCache
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.services.ServiceRegistrationData

internal object SymbolLightClassesForLibraryFrontendApiTestConfiguratorService : FrontendApiTestConfiguratorService {
    override fun TestConfigurationBuilder.configureTest(disposable: Disposable) {
        with(LibraryFrontendApiTestConfiguratorService) {
            configureTest(disposable)
        }
        useAdditionalServices(ServiceRegistrationData(CompiledLibraryProvider::class, ::CompiledLibraryProvider))
    }

    override fun registerProjectServices(project: MockProject) {
        LibraryFrontendApiTestConfiguratorService.registerProjectServices(project)
        project.registerService(ClsJavaStubByVirtualFileCache::class.java)
    }

    override fun registerApplicationServices(application: MockApplication) {
        LibraryFrontendApiTestConfiguratorService.registerApplicationServices(application)
    }

    override fun doOutOfBlockModification(file: KtFile) {
        LibraryFrontendApiTestConfiguratorService.doOutOfBlockModification(file)
    }
}