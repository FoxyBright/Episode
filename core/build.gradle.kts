plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()

kotlin {
    sourceSets.forEach {
        it.kotlin.srcDirs += File("build/generated/ksp/${it.name}/kotlin")
    }
}

dependencies {
    androidBase(true)
}