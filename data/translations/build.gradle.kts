buildscript {
    repositories {
        flatDir {
            dirs("libs")
        }
    }
}

plugins {
    id("com.android.library")
    kotlin("android")
}

apply<DataPlugin>()

baseConfig()

dependencies {
    androidBase()
    dataBase()
    json()

    implementation(files("libs/libwebrtc.aar"))
    implementation(project(":data:shared"))
    implementation(project(":data:meetings"))
    implementation(project(":shared"))
}