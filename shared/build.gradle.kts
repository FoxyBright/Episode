plugins {
    id("com.android.library")
    kotlin("android")
}

apply<DataPlugin>()

baseConfig()
compose()

dependencies {
    androidBase()
    dataBase()
    compose()
    
    implementation("com.airbnb.android:lottie-compose:5.2.0")
    
    // specially low versions are associated with dependency conflict
    implementation("androidx.camera:camera-camera2:1.1.0-alpha11")
    implementation("androidx.camera:camera-view:1.0.0-alpha31")
    implementation("androidx.camera:camera-lifecycle:1.1.0-alpha11")
}