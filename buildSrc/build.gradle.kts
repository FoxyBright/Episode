
repositories {
    mavenCentral()
    google()
}

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation("com.android.tools.build:gradle:8.0.0-alpha08")
    implementation("com.android.tools.build:gradle-api:8.0.0-alpha08")

    val kotlinVer = "1.7.21"
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVer")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVer")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:$kotlinVer")
    implementation("org.jetbrains.kotlin:kotlin-android-extensions:$kotlinVer")
    
    implementation("io.realm.kotlin:gradle-plugin:1.5.0")
}