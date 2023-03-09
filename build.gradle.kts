
buildscript{
    repositories {
        mavenCentral()
        google()
    }
    dependencies{
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}

apply<CiAndroidPlugin>()