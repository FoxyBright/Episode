plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    implementation(project(":domain:paginator"))
    
    implementation(project(":feature:bottomsheet"))
    implementation(project(":feature:complaints"))
    implementation(project(":feature:profile"))
    
    implementation(project(":data:notification"))
    implementation(project(":data:meetings"))
    implementation(project(":data:reports"))
    implementation(project(":data:profile"))
    
    implementation(project(":shared"))
}