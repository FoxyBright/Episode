plugins {
    id("com.android.library")
    kotlin("android")
    id("io.realm.kotlin") version "1.5.0"
}

baseConfig()
compose()

dependencies {
    androidBase(true)
    compose()
    
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("io.realm.kotlin:library-base:1.5.0")
    
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.14.0")
    
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("com.google.truth.extensions:truth-java8-extension:1.1.3")
    testImplementation("org.mockito:mockito-core:4.9.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.4")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test.ext:truth:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.0")
    androidTestUtil("androidx.test:orchestrator:1.4.2")
}