import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.androidBase(excludeCore: Boolean = false) = implementation(
    "androidx.core:core-ktx:1.9.0",
    "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1"
)and koin() and implementationIf(!excludeCore, project(":core"))

const val composeVer = "1.2.1"
fun DependencyHandlerScope.compose() = implementation(
    "androidx.activity:activity-compose:1.6.0",
    "androidx.compose.material3:material3:1.0.0-beta03",
    "androidx.navigation:navigation-compose:2.5.2",
    "io.coil-kt:coil-compose:2.2.1",
    "androidx.compose.ui:ui:$composeVer",
    "androidx.compose.ui:ui-tooling-preview:$composeVer",
) and debugImplementation(
    "androidx.compose.ui:ui-tooling:$composeVer",
    "androidx.compose.ui:ui-test-manifest:$composeVer"
)

const val koinVersion= "3.2.1"
fun DependencyHandlerScope.koin() = implementation(
    "io.insert-koin:koin-core:$koinVersion",
    "io.insert-koin:koin-android:$koinVersion",
    "io.insert-koin:koin-androidx-compose:$koinVersion"
)

@Suppress("UNUSED_PARAMETER")
private infix fun Unit.and(o: Unit) { }

private fun DependencyHandler.implementationIf(condition: Boolean, dependencyNotation: Any) {
    if(condition) implementation(dependencyNotation)
}

private fun DependencyHandler.implementation(vararg dependencyNotations: Any) =
    dependencyNotations.forEach { add("implementation", it) }

private fun DependencyHandler.debugImplementation(vararg dependencyNotations: Any) =
    dependencyNotations.forEach { add("debugImplementation", it) }