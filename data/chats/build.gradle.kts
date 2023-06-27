plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-kapt")
}

apply { plugin("kotlin-kapt") }
apply<DataPlugin>()
baseConfig()

dependencies {
    androidBase()
    dataBase()
    
    kapt("androidx.room:room-compiler:$roomVersion")
    
    implementation(project(":domain:paginator"))
    
    implementation(project(":data:meetings"))
    implementation(project(":data:shared"))
    
    implementation(project(":shared"))
}