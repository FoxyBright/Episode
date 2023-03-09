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
    implementation(project(":feature:animated"))
    implementation(project(":feature:bubbles"))
    implementation(project(":feature:profile"))
    
    implementation(project(":data:meetings"))
    implementation(project(":data:profile"))
    
    implementation(project(":shared"))
}