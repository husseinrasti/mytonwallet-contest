plugins {
    id("build.logic.android.library.feature")
    id("build.logic.android.library.compose")
}

android {
    namespace = "com.husseinrasti.app.feature.auth.ui"
}

dependencies {
    implementation(project(":core:security"))
    implementation(project(":component:ui"))
    implementation(project(":component:theme"))
    implementation(project(":component:navigation"))
    implementation(project(":feature:auth:domain"))
    implementation(libs.lottie.compose)
}