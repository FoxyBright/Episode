plugins {
    id("com.android.library")
    kotlin("android")
}

apply<DataPlugin>()

baseConfig()

android {
    buildFeatures { buildConfig = true }
    defaultConfig {
        buildConfigField("String", "CLIENT_ID", "\"97717bd0-9e33-49e8-aa6f-68c2b54a46be\"")
        buildConfigField("String", "CLIENT_SECRET", "\"nDS818gVflyLgLqFoq5CjE98q8GRYmomWD3umaTI\"")
        buildConfigField("String", "HOST", "\"gilty.rikmasters.ru\"")
        buildConfigField("String", "PREFIX_URL", "\"/api/v1\"")
    }
}

dependencies {
    androidBase()
    dataBase()
    implementation(project(":shared"))
}