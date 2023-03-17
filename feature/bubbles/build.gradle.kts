plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase()
    compose()
    implementation("com.jawnnypoo:physicslayout:3.0.1")
    
//    implementation("com.github.igalata:Bubble-Picker:v0.2.4")
    
}