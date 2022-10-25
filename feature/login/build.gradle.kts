plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    testImplementation("org.testng:testng:6.9.6")
    androidBase()
    compose()
    implementation(project(":shared"))
}