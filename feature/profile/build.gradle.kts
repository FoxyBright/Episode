plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    
    implementation(project(":feature:bottomsheet"))
    implementation(project(":feature:bubbles"))
    
    implementation(project(":domain:paginator"))
    
    implementation(project(":data:meetings"))
    implementation(project(":data:profile"))
    implementation(project(":data:chats"))
    implementation(project(":data:auth"))
    
    implementation(project(":shared"))
}