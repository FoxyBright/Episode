import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.base() = implementationAll(
    "androidx.core:core-ktx:1.9.0",
    "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1"
)

val composeVer = "1.2.1"

fun DependencyHandlerScope.compose() = implementationAll(
    "androidx.activity:activity-compose:1.6.0",
    "androidx.compose.material3:material3:1.0.0-beta03",
    "androidx.compose.ui:ui:$composeVer",
    "androidx.compose.ui:ui-tooling-preview:$composeVer",
) and debugImplementationAll(
    "androidx.compose.ui:ui-tooling:$composeVer",
    "androidx.compose.ui:ui-test-manifest:$composeVer"
)


@Suppress("UNUSED_PARAMETER")
private infix fun Unit.and(o: Unit) { }

private fun DependencyHandler.implementationAll(vararg dependencyNotations: String) =
    dependencyNotations.forEach { add("implementation", it) }

private fun DependencyHandler.debugImplementationAll(vararg dependencyNotations: String) =
    dependencyNotations.forEach { add("debugImplementation", it) }