
repositories {
    mavenCentral()
    google()
}

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation("com.android.tools.build:gradle:8.0.0-alpha01")
    implementation("com.android.tools.build:gradle-api:8.0.0-alpha01")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.7.10")
    implementation("org.jetbrains.kotlin:kotlin-android-extensions:1.7.10")
}