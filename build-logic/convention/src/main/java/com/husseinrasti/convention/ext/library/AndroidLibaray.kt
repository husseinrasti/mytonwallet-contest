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

package com.husseinrasti.convention.ext.library


import com.android.build.gradle.LibraryExtension
import com.husseinrasti.convention.base.*
import com.husseinrasti.convention.ext.kotlinOptions
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate


internal fun Project.android(configure: Action<LibraryExtension>): Unit =
    (this as ExtensionAware).extensions.configure("android", configure)


internal fun Project.configureLibraryFlavor() {
    android {
        buildTypes.apply {
            getByName(BuildType.RELEASE) {
                isMinifyEnabled = BuildTypeRelease.isMinifyEnabled
                isTestCoverageEnabled = BuildTypeRelease.isTestCoverageEnabled
            }
            getByName(BuildType.DEBUG) {
                isMinifyEnabled = BuildTypeDebug.isMinifyEnabled
                isTestCoverageEnabled = BuildTypeDebug.isTestCoverageEnabled
                extra["enableCrashlytics"] = false
                extra["alwaysUpdateBuildId"] = false
            }
        }


        flavorDimensions.add(BuildProductDimensions.ENVIRONMENT)
        productFlavors.apply {
            ProductFlavorDevelop.libraryCreate(this)
            ProductFlavorProduction.libraryCreate(this)
        }
    }
}

fun Project.androidLibrary() {
    android {
        compileSdk = BuildAndroidConfig.COMPILE_SDK_VERSION
        defaultConfig.minSdk = BuildAndroidConfig.MIN_SDK_VERSION
        defaultConfig.targetSdk = BuildAndroidConfig.TARGET_SDK_VERSION
        defaultConfig.multiDexEnabled = true

        buildFeatures.viewBinding = true

        configureLibraryFlavor()

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

            // Set JVM target to 11
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
}