@file:Suppress("UnstableApiUsage")

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}

plugins {
    id("com.google.gms.google-services") version "4.3.15"
    id("com.android.application")
    id("androidx.navigation.safeargs")
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
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.6")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")
    
    androidBase()
    compose()
    
    implementation(project(":feature:notifications"))
    implementation(project(":feature:bottomsheet"))
    implementation(project(":feature:mainscreen"))
    implementation(project(":feature:yandexmap"))
    implementation(project(":feature:animated"))
    implementation(project(":feature:addmeet"))
    implementation(project(":feature:bubbles"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:gallery"))
    implementation(project(":feature:login"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:translation"))
    
    implementation(project(":domain:paginator"))
    implementation(project(":domain"))
    
    implementation(project(":data:notifications"))
    implementation(project(":data:meetings"))
    implementation(project(":data:profile"))
    implementation(project(":data:reports"))
    implementation(project(":data:shared"))
    implementation(project(":data:chats"))
    implementation(project(":data:realm"))
    implementation(project(":data:auth"))
    implementation(project(":data:ktor"))
    implementation(project(":data:translations"))
    
    implementation(project(":example"))
    implementation(project(":shared"))
}