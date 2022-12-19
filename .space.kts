@file:Suppress("BlockingMethodInNonBlockingContext")

import io.ktor.utils.io.core.use
import java.io.FileOutputStream
import java.util.Base64
import java.io.File

val androidRepository = "android-mirror"
val internalTestBranch = "refs/heads/develop"

job("Сборка и публикация (внутренний тест)") {
    startOn {
        gitPush {
            branchFilter = internalTestBranch
        }
    }
    
    host {
        requirements {
            workerTags("androidsdk")
        }
        
        env["CI_ACTIVE"] = "true"
        env["GOOGLE_SA_KEY"] = Secrets("google_sa_key")
        env["KEY_STORE"] = Secrets("keystore")
        env["KEY_STORE_PASS"] = Secrets("keystore_pass")
        env["KEY_PASS"] = Secrets("key_pass")
        env["KEY_ALIAS"] = Secrets("key_alias")
        
        kotlinScript { api ->
            val jbExecNumber = System.getenv("JB_SPACE_EXECUTION_NUMBER")
            val decoder = Base64.getDecoder()
            
            val googleSaKeyFile = File("google_sa_key.json")
            FileOutputStream(googleSaKeyFile).use { os ->
                System.getenv("GOOGLE_SA_KEY")
                    .byteInputStream().copyTo(os)
            }
            env["GOOGLE_SA_KEY_PATH"] = googleSaKeyFile.absolutePath
            
            val keystoreFile = File("keystore.jks")
            FileOutputStream(keystoreFile).use { os ->
                decoder.decode(System.getenv("KEY_STORE"))
                    .inputStream().copyTo(os)
            }
            env["KEY_STORE_PATH"] = keystoreFile.absolutePath
            
            env["PUBLISH_TRACK"] = "internal"
            env["PUBLISH_RELEASE_NAME"] = "Release $jbExecNumber"
            env["PUBLISH_VERSION_CODE"] = jbExecNumber
            env["PUBLISH_VERSION_NAME"] = "INTERNAL-$jbExecNumber"
            
            api.gradlew("ciPublish") // Падает из-за окончание строки -> env: sh\r: No such file or directory
        }
    }
}