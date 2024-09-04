plugins {
    id("build.logic.android.application")
}

android {
    namespace = "com.husseinrasti.app.mytonwallet"
}

dependencies {
    implementation(project(":component:ui"))
    implementation(project(":component:navigation"))
    implementation(project(":core:dagger-hilt"))
    implementation(project(":core:datastore"))
    implementation(project(":core:security"))
    implementation(project(":feature:create-wallet:ui"))
    implementation(project(":feature:create-wallet:data"))
    implementation(project(":feature:create-wallet:domain"))
    implementation(project(":feature:auth:ui"))
    implementation(project(":feature:auth:data"))
    implementation(project(":feature:auth:domain"))
}