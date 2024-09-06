plugins {
    id("build.logic.android.library.feature")
}

android {
    namespace = "com.husseinrasti.app.feature.create.data"
}

dependencies {
    implementation(project(":feature:create-wallet:domain"))
    implementation(project(":core:datastore"))
    implementation(project(":core:dagger-hilt"))
    implementation(project(":core:security"))
}