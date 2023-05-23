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
    implementation(project(":feature:gallery"))
    
    implementation(project(":domain:paginator"))
    
    implementation(project(":data:meetings"))
    implementation(project(":data:profile"))
    implementation(project(":data:chats"))
    implementation(project(":data:auth"))
    implementation(project(":data:translations"))

    implementation(project(":shared"))
}