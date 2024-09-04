import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.husseinrasti.convention"
//version = "1.0.0"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        create("coreAndroidApplication") {
            id = "build.logic.android.application"
            implementationClass = "com.husseinrasti.convention.plugins.AndroidApplicationConventionPlugin"
        }
        create("coreAndroidLibrary") {
            id = "build.logic.android.library"
            implementationClass = "com.husseinrasti.convention.plugins.AndroidLibraryConventionPlugin"
        }
        create("buildLogicLibraryCompose") {
            id = "build.logic.android.library.compose"
            implementationClass = "com.husseinrasti.convention.plugins.AndroidLibraryComposeConventionPlugin"
        }
        create("buildLogicLibraryFeature") {
            id = "build.logic.android.library.feature"
            implementationClass = "com.husseinrasti.convention.plugins.AndroidFeatureConventionPlugin"
        }
        create("buildLogicLibraryHilt") {
            id = "build.logic.android.hilt"
            implementationClass = "com.husseinrasti.convention.plugins.AndroidHiltConventionPlugin"
        }
    }
}