import org.gradle.api.JavaVersion

object Config {

    const val compileSdk = 33
    const val targetSdk = compileSdk
    const val minSdk = 24
    const val versionCode = 1
    const val versionName = "1.0"

    const val applicationId = "ru.rikmasters.gilty"
    const val namespace = applicationId

    const val jvmTarget = "1.8"

    val sourceCompatibility = JavaVersion.VERSION_1_8
    val targetCompatibility = sourceCompatibility
}