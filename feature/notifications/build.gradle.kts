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
    implementation(project(":feature:profile"))
    
    implementation(project(":domain:paginator"))
    
    implementation(project(":data:notifications"))
    implementation(project(":data:meetings"))
    implementation(project(":data:reports"))
    implementation(project(":data:profile"))
    
    implementation(project(":shared"))
}