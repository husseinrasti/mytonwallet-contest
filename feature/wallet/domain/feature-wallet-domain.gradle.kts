plugins {
    id("build.logic.android.library.feature")
}

android {
    namespace = "com.husseinrasti.app.feature.wallet.domain"
}

dependencies {
    implementation(project(":core:dagger-hilt"))
}