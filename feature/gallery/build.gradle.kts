plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    imageCropper()
    androidBase()
    compose()
    
    implementation(project(":shared"))
}