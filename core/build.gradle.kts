plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()

dependencies {
    androidBase()

    implementation("org.reflections:reflections:0.10.2")
}