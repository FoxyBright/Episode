plugins {
    kotlin("jvm")
}

baseConfig()

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.10-1.0.6")
}