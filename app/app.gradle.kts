plugins {
    id("build.logic.android.application")
}

android {
    namespace = "com.husseinrasti.app.mytonwallet"
}

dependencies {
    implementation(project(":component:ui"))
    implementation(project(":component:theme"))
    implementation(project(":core:dagger-hilt"))
    implementation(project(":core:datastore"))
    implementation(project(":core:security"))
}