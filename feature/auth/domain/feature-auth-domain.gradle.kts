plugins {
    id("build.logic.android.library.feature")
}

android {
    namespace = "com.husseinrasti.app.feature.auth.domain"
}

dependencies {
    implementation(project(":core:dagger-hilt"))
}