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


compose()
baseConfig()

dependencies {
    androidBase()
    compose()
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.6")
    implementation("io.getstream:stream-webrtc-android:1.0.2")
    implementation("com.github.Armen101:AudioRecordView:1.0.1")
    implementation(project(":shared"))
    implementation(project(":data:meetings"))
    implementation(project(":data:translations"))
    implementation(project(":feature:addmeet"))
    implementation(project(":feature:bottomsheet"))
}