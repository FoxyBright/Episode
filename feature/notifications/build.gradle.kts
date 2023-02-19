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
    implementation(project(":feature:profile"))
    implementation(project(":feature:bottomsheet"))
    
    implementation(project(":data:meetings"))
    implementation(project(":data:profile"))
    implementation(project(":data:notification"))
    
    implementation(project(":shared"))
}