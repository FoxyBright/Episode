plugins {
    id("com.android.library")
    kotlin("android")
}

apply<DataPlugin>()

baseConfig()

dependencies {
    androidBase()
    dataBase()
}