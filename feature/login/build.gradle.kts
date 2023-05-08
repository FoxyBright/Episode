plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    
    implementation(project(":feature:mainscreen"))
    implementation(project(":feature:animated"))
    implementation(project(":feature:bubbles"))
    implementation(project(":feature:gallery"))
    
    implementation(project(":data:meetings"))
    implementation(project(":data:auth"))
    
    implementation(project(":shared"))
}