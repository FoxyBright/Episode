import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class CiPlugin: Plugin<Project> {
    
    protected val env by lazy { System.getenv() }
}