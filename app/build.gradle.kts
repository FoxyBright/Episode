@file:Suppress("UnstableApiUsage")

plugins {
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

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    androidBase()
    compose()
    
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")
    
    implementation(project(":shared"))
    implementation(project(":example"))
    implementation(project(":feature:login"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:addmeet"))
    implementation(project(":feature:mainscreen"))
    implementation(project(":feature:complaints"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:notifications"))
}