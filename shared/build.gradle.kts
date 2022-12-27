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
}