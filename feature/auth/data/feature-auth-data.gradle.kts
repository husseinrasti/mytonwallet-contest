plugins {
    id("build.logic.android.library.feature")
}

android {
    namespace = "com.husseinrasti.app.feature.auth.data"
}

dependencies {
    implementation(project(":feature:auth:domain"))
    implementation(project(":core:datastore"))
    implementation(project(":core:dagger-hilt"))
}