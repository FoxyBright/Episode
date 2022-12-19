import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.github.triplet.gradle.play.PlayPublisherExtension
import org.gradle.api.Project
import kotlin.random.Random

class CiAndroidPublishPlugin: CiPlugin() {
    
    private val googleSaKeyPath by lazy { env["GOOGLE_SA_KEY_PATH"]!! }
    
    private val keyStorePath by lazy { env["KEY_STORE_PATH"]!! }
    private val keyStorePass by lazy { env["KEY_STORE_PASS"]!! }
    private val keyPass by lazy { env["KEY_PASS"]!! }
    private val keyAlias by lazy { env["KEY_ALIAS"]!! }
    
    private val publishTrack by lazy { env["PUBLISH_TRACK"] ?: "internal" }
    private val publishPriority by lazy { env["PUBLISH_PRIORITY"]?.toInt() ?: 3 }
    private val publishReleaseName by lazy { env["PUBLISH_RELEASE_NAME"] ?: "CI release" }
    private val publishListing by lazy { env["PUBLISH_LISTING"]?.toBoolean() ?: true }
    
    private val randomVersionCode by lazy { Random.nextInt() }
    private val publishVersionCode by lazy { env["PUBLISH_VERSION_CODE"]?.toInt() ?: randomVersionCode }
    private val publishVersionName by lazy { env["PUBLISH_VERSION_NAME"] ?: "UNNAMED-$publishVersionCode" }
    
    //private val  by lazy { env[""] }
    
    override fun apply(target: Project): Unit = target.run {
        pluginManager.apply("com.github.triplet.play")
        extensions.configure<PlayPublisherExtension>("play") {
            serviceAccountCredentials.set(file(googleSaKeyPath))
            track.set(publishTrack)
            updatePriority.set(publishPriority)
            releaseName.set(publishReleaseName)
            defaultToAppBundles.set(true)
        }
        extensions.configure<BaseAppModuleExtension>("android") {
            defaultConfig {
                versionName = publishVersionName
                versionCode = publishVersionCode
                signingConfigs {
                    register("ci") {
                        storeFile = file(keyStorePath)
                        storePassword = keyStorePass
                        keyAlias = this@CiAndroidPublishPlugin.keyAlias
                        keyPassword = keyPass
                    }
                }
            }
            buildTypes.getByName("release") {
                signingConfig = signingConfigs.getByName("ci")
            }
        }
        tasks.register("ciPublish") {
            dependsOn.add("publish")
            if(publishListing)
                dependsOn.add("publishListing")
        }
    }
}