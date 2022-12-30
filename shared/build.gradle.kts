plugins {
    id("com.android.library")
    kotlin("android")
}

apply<DataPlugin>()

baseConfig()
compose()

dependencies {
    androidBase()
    dataBase()
    compose()
    implementation("com.airbnb.android:lottie-compose:5.2.0")
}