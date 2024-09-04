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

package com.husseinrasti.convention.base


object BuildAndroidConfig {

    const val APPLICATION_ID = "com.husseinrasti.app.mytonwallet"

    const val COMPILE_SDK_VERSION = 34
    const val MIN_SDK_VERSION = 23
    const val TARGET_SDK_VERSION = 34

    const val VERSION_CODE = 1
    const val VERSION_NAME = "0.0.1 ($VERSION_CODE)"

    const val SUPPORT_LIBRARY_VECTOR_DRAWABLES = true

    const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
}