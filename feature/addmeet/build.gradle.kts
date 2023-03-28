plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    
    implementation(project(":feature:bottomsheet"))
    implementation(project(":feature:bubbles"))
    
    implementation(project(":data:meetings"))
    implementation(project(":data:profile"))
    
    implementation(project(":shared"))
}