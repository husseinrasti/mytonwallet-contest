plugins {
    id("build.logic.android.library")
}

android {
    namespace = "com.husseinrasti.app.core.datastore"
}

dependencies {
    implementation(project(":core:dagger-hilt"))
}