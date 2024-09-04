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

import com.husseinrasti.convention.ext.app.androidApplication
import com.husseinrasti.convention.ext.findLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.kapt")
            pluginManager.apply("build.logic.android.hilt")
            androidApplication()
            dependencies {
                add("implementation", platform(findLibrary("androidx-compose-bom")))
                add("androidTestImplementation", platform(findLibrary("androidx-compose-bom")))
                add("implementation", findLibrary("coil.kt"))
                add("implementation", findLibrary("coil.kt.compose"))
                add("implementation", findLibrary("androidx.core.ktx"))
                add("implementation", findLibrary("androidx.hilt.navigation.compose"))
                add("implementation", findLibrary("androidx.activity.compose"))
                add("implementation", findLibrary("androidx.compose.ui.tooling"))
                add("implementation", findLibrary("androidx.compose.ui.util"))
                add("implementation", findLibrary("androidx.compose.foundation"))
                add("implementation", findLibrary("androidx.compose.runtime"))
                add("implementation", findLibrary("androidx.compose.material"))
                add("implementation", findLibrary("androidx.lifecycle.runtimeCompose"))
                add("implementation", findLibrary("androidx.lifecycle.viewModelCompose"))
            }
        }
    }
}