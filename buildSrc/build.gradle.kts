
repositories {
    mavenCentral()
    google()
}

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

@Suppress("GradleDependency")
dependencies {
    implementation("com.android.tools.build:gradle-api:8.1.0-alpha09")
    implementation("com.android.tools.build:gradle:8.1.0-alpha09")
    
    val kotlinVer = "1.7.21"
    implementation("org.jetbrains.kotlin:kotlin-android-extensions:$kotlinVer")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:$kotlinVer")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVer")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVer")
    
    implementation("com.github.triplet.gradle:play-publisher:3.7.0")
    implementation("io.realm.kotlin:gradle-plugin:1.5.0")
}