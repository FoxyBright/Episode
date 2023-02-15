plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    
    implementation(project(":feature:complaints"))
    implementation(project(":data:meetings"))
    implementation(project(":data:profile"))
    implementation(project(":shared"))
}