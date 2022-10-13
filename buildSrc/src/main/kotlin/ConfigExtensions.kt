import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.dsl.DynamicFeatureExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

fun Project.baseConfig(name: String = project.name) {

    (this as ExtensionAware).extensions.run {

        findByType<BaseAppModuleExtension>()?.apply {
            baseConfig(name)
            baseAppModuleConfig()
            return@run
        }

        findByType<LibraryExtension>()?.apply {
            baseConfig(name)
            libraryConfig()
            return@run
        }

        findByType<DynamicFeatureExtension>()?.apply {
            baseConfig(name)
            dynamicFeatureConfig()
            return@run
        }
    }
}

fun BaseAppModuleExtension.baseAppModuleConfig() {

    compileSdk = Config.compileSdk

    defaultConfig {
        applicationId = Config.applicationId
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

fun LibraryExtension.libraryConfig() {

    compileSdk = Config.compileSdk
}

fun DynamicFeatureExtension.dynamicFeatureConfig() {

}

fun BaseExtension.baseConfig(name: String) {

    namespace = if(name == "app") Config.applicationId
    else "${Config.namespacePrefix}.$name"

    defaultConfig {
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        versionCode = Config.versionCode
        versionName = Config.versionName
    }

    compileOptions {
        sourceCompatibility = Config.sourceCompatibility
        targetCompatibility = Config.targetCompatibility
    }
    (this as ExtensionAware).configure<KotlinJvmOptions> {
        jvmTarget = Config.jvmTarget
    }
}