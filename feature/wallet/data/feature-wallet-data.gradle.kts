plugins {
    id("build.logic.android.library.feature")
}

android {
    namespace = "com.husseinrasti.app.feature.wallet.data"
}

dependencies {
    implementation(project(":feature:wallet:domain"))
    implementation(project(":core:datastore"))
    implementation(project(":core:dagger-hilt"))
    implementation(project(":core:security"))
}