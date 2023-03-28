plugins {
    id("com.android.library")
    kotlin("android")
}

apply<DataPlugin>()

baseConfig()

dependencies {
    androidBase()
    dataBase()
    
    implementation(project(":data:notifications"))
    implementation(project(":data:shared"))
    
    implementation(project(":shared"))
}