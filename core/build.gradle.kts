plugins {
    id("com.android.library")
    kotlin("android")
    id("com.google.devtools.ksp")
}

baseConfig()

kotlin {
    sourceSets.forEach {
        it.kotlin.srcDirs += File("build/generated/ksp/${it.name}/kotlin")
    }
}

dependencies {
    androidBase(true)

    implementation(project(":core:annotation"))
    ksp(project(":core:annotation"))
}