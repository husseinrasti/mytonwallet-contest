plugins {
    id("build.logic.android.library.compose")
}

android {
    namespace = "com.husseinrasti.app.component.ui"
}

dependencies {
    implementation(project(":component:theme"))
}