plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    
    implementation(project(":shared"))
}