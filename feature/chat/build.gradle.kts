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
    implementation(project(":feature:mainscreen"))
    implementation(project(":feature:animated"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:gallery"))
    implementation(project(":feature:translation"))

    implementation(project(":domain:paginator"))
    
    implementation(project(":data:meetings"))
    implementation(project(":data:profile"))
    implementation(project(":data:chats"))
    implementation(project(":data:translations"))
    
    implementation(project(":shared"))
}