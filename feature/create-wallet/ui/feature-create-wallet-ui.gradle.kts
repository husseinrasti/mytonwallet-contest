plugins {
    id("build.logic.android.library.feature")
    id("build.logic.android.library.compose")
}

android {
    namespace = "com.husseinrasti.app.feature.create.ui"
}

dependencies {
    implementation(project(":component:ui"))
    implementation(project(":component:theme"))
    implementation(project(":component:navigation"))
    implementation(project(":feature:create-wallet:domain"))
    implementation(project(":feature:auth:domain"))
}