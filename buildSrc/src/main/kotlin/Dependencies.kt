import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.base() = implementationAll(
    "androidx.core:core-ktx:1.9.0",
    "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1"
) and koin()

val composeVer = "1.2.1"
fun DependencyHandlerScope.compose() = implementationAll(
    "androidx.activity:activity-compose:1.6.0",
    "androidx.compose.material3:material3:1.0.0-beta03",
    "androidx.navigation:navigation-compose:2.5.2",
    "io.coil-kt:coil-compose:2.2.1",
    "androidx.compose.ui:ui:$composeVer",
    "androidx.compose.ui:ui-tooling-preview:$composeVer",
) and debugImplementationAll(
    "androidx.compose.ui:ui-tooling:$composeVer",
    "androidx.compose.ui:ui-test-manifest:$composeVer"
)

val koin_version= "3.2.1"
fun DependencyHandlerScope.koin() = implementationAll(
    "io.insert-koin:koin-core:$koin_version",
    "io.insert-koin:koin-android:$koin_version",
    "io.insert-koin:koin-androidx-compose:$koin_version"
)

@Suppress("UNUSED_PARAMETER")
private infix fun Unit.and(o: Unit) { }

private fun DependencyHandler.implementationAll(vararg dependencyNotations: String) =
    dependencyNotations.forEach { add("implementation", it) }

private fun DependencyHandler.debugImplementationAll(vararg dependencyNotations: String) =
    dependencyNotations.forEach { add("debugImplementation", it) }