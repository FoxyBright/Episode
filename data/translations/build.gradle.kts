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
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.6")

    implementation("io.getstream:stream-webrtc-android:1.0.2")
    
    implementation(project(":data:shared"))
    implementation(project(":data:meetings"))
    implementation(project(":shared"))
}