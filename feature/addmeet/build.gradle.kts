plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    implementation(project(":shared"))
    implementation(project(":feature:bubbles"))
}