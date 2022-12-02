plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    implementation("androidx.compose.material:material:1.3.1")
    androidBase()
    compose()
}