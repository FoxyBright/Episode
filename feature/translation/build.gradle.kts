buildscript {
    repositories {
        flatDir {
            dirs("libs")
        }
    }
}

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    
    implementation("io.getstream:stream-webrtc-android:1.0.2")
    
    implementation(project(":shared"))
    implementation(project(":data:meetings"))
    implementation(project(":data:translations"))
    implementation(project(":feature:addmeet"))
}