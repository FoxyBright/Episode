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

    implementation("io.getstream:stream-webrtc-android:1.0.2")
    
    implementation(project(":data:shared"))
    implementation(project(":data:meetings"))
    implementation(project(":shared"))
}