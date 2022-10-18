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

    implementation(project(":example"))
}