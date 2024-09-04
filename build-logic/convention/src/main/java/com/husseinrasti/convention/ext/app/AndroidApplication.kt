/*
 * Copyright (C) 2022  The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.husseinrasti.convention.ext.app


import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.husseinrasti.convention.base.*
import com.husseinrasti.convention.ext.findLibrary
import com.husseinrasti.convention.ext.kapt
import com.husseinrasti.convention.ext.kotlinOptions
import com.husseinrasti.convention.ext.libs
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate


internal fun Project.android(configure: Action<BaseAppModuleExtension>): Unit =
    (this as ExtensionAware).extensions.configure("android", configure)


internal fun Project.configureApplicationFlavor() {
    android {
        buildTypes.apply {
            getByName(BuildType.RELEASE) {
                proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
                isMinifyEnabled = BuildTypeRelease.isMinifyEnabled
                isShrinkResources = BuildTypeRelease.isShrinkResources
                isTestCoverageEnabled = BuildTypeRelease.isTestCoverageEnabled
            }

            getByName(BuildType.DEBUG) {
                isMinifyEnabled = BuildTypeDebug.isMinifyEnabled
                isShrinkResources = BuildTypeDebug.isShrinkResources
                isTestCoverageEnabled = BuildTypeDebug.isTestCoverageEnabled
                extra["enableCrashlytics"] = false
                extra["alwaysUpdateBuildId"] = false
            }
        }

        flavorDimensions.add(BuildProductDimensions.ENVIRONMENT)
        productFlavors.apply {
            ProductFlavorDevelop.appCreate(this)
            ProductFlavorProduction.appCreate(this)
        }
    }
}


fun Project.androidApplication() {
    android {
        compileSdk = BuildAndroidConfig.COMPILE_SDK_VERSION
        defaultConfig.minSdk = BuildAndroidConfig.MIN_SDK_VERSION
        defaultConfig.targetSdk = BuildAndroidConfig.TARGET_SDK_VERSION
        defaultConfig.multiDexEnabled = true
        defaultConfig.versionCode = BuildAndroidConfig.VERSION_CODE
        defaultConfig.versionName = BuildAndroidConfig.VERSION_NAME
        defaultConfig.vectorDrawables {
            useSupportLibrary = BuildAndroidConfig.SUPPORT_LIBRARY_VECTOR_DRAWABLES
        }
        defaultConfig.testInstrumentationRunner = BuildAndroidConfig.TEST_INSTRUMENTATION_RUNNER

        applicationVariants.all {
            val variant = this
            variant.outputs
                .map { it as BaseVariantOutputImpl }
                .forEach { output ->
                    output.outputFileName = "MyTonWallet_${variant.name}_v.${variant.versionName}.apk"
                }
        }

        configureApplicationFlavor()

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        kotlinOptions {
            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()

            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental",
            )

            // Set JVM target to 17
            jvmTarget = JavaVersion.VERSION_17.toString()
        }

        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs().findVersion("androidxComposeCompiler").get().toString()
        }
    }

    kapt {
        correctErrorTypes = true
    }

    dependencies {
        add("coreLibraryDesugaring", findLibrary("android.desugarJdkLibs"))
    }

}
