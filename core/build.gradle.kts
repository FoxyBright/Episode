import android.annotation.SuppressLint

plugins {
    id("com.android.library")
    kotlin("android")
}

baseConfig()
compose()

dependencies {
    androidBase(true)
    compose()
    
    testImplementation("com.google.truth.extensions:truth-java8-extension:1.1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.4")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.ext:truth:1.5.0")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.1")
    androidTestImplementation("androidx.test:core:1.5.0")
    testImplementation("org.mockito:mockito-core:4.9.0")
    androidTestUtil("androidx.test:orchestrator:1.4.2")
    testImplementation("com.google.truth:truth:1.1.3")
    implementation("androidx.browser:browser:1.4.0")
    implementation(kotlin("reflect"))
}
