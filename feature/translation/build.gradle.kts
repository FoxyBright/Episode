plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()

    implementation(project(":shared"))
}