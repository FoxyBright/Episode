@file:Suppress("UnstableApiUsage")

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
    }
}

plugins {
    id("com.google.gms.google-services") version "4.3.15"
    id("com.android.application")
    kotlin("android")
}

baseConfig()
compose()

android {
    
    defaultConfig {
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    androidBase()
    compose()
    
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")
    
    implementation(project(":feature:notifications"))
    implementation(project(":feature:mainscreen"))
    implementation(project(":feature:complaints"))
    implementation(project(":feature:animated"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:addmeet"))
    implementation(project(":feature:login"))
    implementation(project(":feature:chat"))
    
    implementation(project(":domain:paginator"))
    implementation(project(":domain"))
    
    implementation(project(":data:shared"))
    implementation(project(":data:chats"))
    implementation(project(":data:realm"))
    implementation(project(":data:auth"))
    implementation(project(":data:ktor"))
    
    implementation(project(":example"))
    implementation(project(":shared"))
}