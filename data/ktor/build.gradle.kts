plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()

dependencies {
    androidBase()
    jackson()
    ktor()
}