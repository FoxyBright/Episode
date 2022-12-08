import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.androidBase(excludeCore: Boolean = false) = implementation(
    "androidx.core:core-ktx:1.9.0",
    "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
)and koin() and implementationIf(!excludeCore, project(":core"))

const val composeVer = Config.kotlinCompilerExtensionVersion
fun DependencyHandlerScope.compose() = implementation(
    "androidx.activity:activity-compose:1.6.0",
    "androidx.compose.material3:material3:1.0.0-rc01",
    "androidx.navigation:navigation-compose:2.5.2",
    "io.coil-kt:coil-compose:2.2.2",
    "androidx.compose.ui:ui:$composeVer",
) and debugImplementation(
    "androidx.compose.ui:ui-tooling:$composeVer",
    "androidx.compose.ui:ui-tooling-preview:$composeVer",
    "androidx.compose.ui:ui-test-manifest:$composeVer"
) and accompanist()

const val accompanistVer = "0.26.5-rc"
fun DependencyHandlerScope.accompanist() = implementation(
    "com.google.accompanist:accompanist-systemuicontroller:$accompanistVer"
)

const val koinVer = "3.2.1"
fun DependencyHandlerScope.koin() = implementation(
    "io.insert-koin:koin-core:$koinVer",
    "io.insert-koin:koin-android:$koinVer",
    "io.insert-koin:koin-androidx-compose:$koinVer"
)

fun DependencyHandlerScope.dataBase() =
    realm() and implementation(project(":data:realm"))

fun DependencyHandlerScope.realm() = implementation(
    "io.realm.kotlin:library-base:1.5.0"
)

fun DependencyHandlerScope.jackson() = implementation(
    "com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.14.0"
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