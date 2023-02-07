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
    implementation(project(":feature:complaints"))
    implementation(project(":feature:bubbles"))
    implementation(project(":data:profile"))
    implementation(project(":data:meetings"))
    implementation(project(":data:auth"))
    implementation("com.vanniktech:android-image-cropper:4.5.0")
}