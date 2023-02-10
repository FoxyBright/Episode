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
    implementation(project(":data:push"))
    implementation(project(":data:profile"))
    implementation(project(":data:meetings"))
}