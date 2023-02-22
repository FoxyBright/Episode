plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    
    implementation(project(":shared"))
    implementation(project(":feature:mainscreen"))
    implementation(project(":feature:complaints"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:animated"))
    
    implementation(project(":data:chats"))
    implementation(project(":data:meetings"))
    implementation(project(":data:profile"))
}