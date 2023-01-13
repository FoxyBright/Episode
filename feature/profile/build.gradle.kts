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
    implementation(project(":feature:complaints")) // TODO КОСТЫЛЬ
    implementation(project(":feature:bubbles"))
    implementation(project(":data:auth"))
}