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

package com.husseinrasti.convention.plugins

import com.husseinrasti.convention.ext.findLibrary
import com.husseinrasti.convention.ext.library.androidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.kapt")
            pluginManager.apply("build.logic.android.hilt")
            androidLibrary()
            dependencies {
                add("testImplementation", kotlin("test"))
                add("androidTestImplementation", kotlin("test"))
                add("implementation", findLibrary("kotlinx.coroutines.android"))
                add("implementation", findLibrary("google.gson"))
                add("implementation", findLibrary("androidx.dataStore.core"))
                add("implementation", findLibrary("androidx.dataStore.preferences"))
                add("implementation", findLibrary("androidx.security.crypto"))
                add("testImplementation", findLibrary("junit"))
                add("androidTestImplementation", findLibrary("androidx.junit"))
                add("androidTestImplementation", findLibrary("androidx.espresso.core"))
                add("androidTestImplementation", platform(findLibrary("androidx.compose.bom")))
                add("androidTestImplementation", findLibrary("androidx.ui.test.junit4"))
            }
        }
    }
}