plugins {
    id("com.android.library")
    kotlin("android")
}

apply<DataPlugin>()

baseConfig()

dependencies {
    implementation("androidx.paging:paging-common-ktx:3.1.1")
    androidBase()
    dataBase()
    
    implementation(project(":data:shared"))
    implementation(project(":shared"))
}