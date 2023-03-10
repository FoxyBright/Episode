plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    ktor()
    
    implementation(project(":feature:yandexmap"))
    
    implementation(project(":data:meetings"))
    implementation(project(":data:profile"))
    implementation(project(":data:reports"))
    
    implementation(project(":shared"))
}