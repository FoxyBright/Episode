import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class CiAndroidPlugin: CiPlugin() {
    
    override fun apply(target: Project) {
        if(env["CI_ACTIVE"].equals("true", true).not()) return
        
        target.childProjects.values
            .forEach {
                it.pluginManager.withPlugin("com.android.application") {
                    it.apply<CiAndroidPublishPlugin>()
                }
            }
    }
}
