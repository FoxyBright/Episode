plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    ktor()
    
    implementation(project(":shared"))
    implementation(project(":data:meetings"))
}