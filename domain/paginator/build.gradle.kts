plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()

dependencies {
    androidBase()
    
    implementation(project(":data:shared"))
    
    implementation(project(":shared"))
}