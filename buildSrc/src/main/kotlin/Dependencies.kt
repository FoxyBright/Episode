import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.androidBase(excludeCore: Boolean = false) = implementation(
    "androidx.core:core-ktx:1.9.0",
    "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
) and koin() and implementationIf(!excludeCore, project(":core"))

const val composeVer = Config.kotlinCompilerExtensionVersion
fun DependencyHandlerScope.compose() = implementation(
    "androidx.activity:activity-compose:1.6.0",
    "androidx.compose.material3:material3:1.0.0-rc01",
    "androidx.navigation:navigation-compose:2.5.2",
    "io.coil-kt:coil-compose:2.2.2",
    "androidx.compose.ui:ui:$composeVer",
) and implementation(
    "androidx.compose.ui:ui-tooling:$composeVer",
    "androidx.compose.ui:ui-tooling-preview:$composeVer",
    "androidx.compose.ui:ui-test-manifest:$composeVer",
) and accompanist(
) and accompanistPermissions(
) and firebase()

const val accompanistVer = "0.26.5-rc"

fun DependencyHandlerScope.firebase() = implementation(
    "com.google.firebase:firebase-bom:31.2.0",
    "com.google.firebase:firebase-messaging:23.1.1",
    "com.google.firebase:firebase-analytics:21.2.0",
)

fun DependencyHandlerScope.accompanist() = implementation(
    "com.google.accompanist:accompanist-systemuicontroller:$accompanistVer"
)

const val accompanistPermissionsVer = "0.20.3"
fun DependencyHandlerScope.accompanistPermissions() = implementation(
    "com.google.accompanist:accompanist-permissions:$accompanistPermissionsVer"
)

const val koinVer = "3.2.1"
fun DependencyHandlerScope.koin() = implementation(
    "io.insert-koin:koin-core:$koinVer",
    "io.insert-koin:koin-android:$koinVer",
    "io.insert-koin:koin-androidx-compose:$koinVer"
)

fun DependencyHandlerScope.dataBase() =
    realm() and ktor() and implementation(
        project(":data:realm"),
        project(":data:ktor")
    )

fun DependencyHandlerScope.realm() = implementation(
    "io.realm.kotlin:library-base:1.5.0"
)


val jacksonVer = "2.14.0"
fun DependencyHandlerScope.jackson() = implementation(
    "com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVer",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:$jacksonVer"
)

val ktorVer = "2.2.1"
fun DependencyHandlerScope.ktor() = implementation(
    "io.ktor:ktor-client-core:$ktorVer",
    "io.ktor:ktor-client-okhttp:$ktorVer",
    "io.ktor:ktor-client-logging:$ktorVer",
    "io.ktor:ktor-client-auth:$ktorVer",
    "io.ktor:ktor-client-content-negotiation:$ktorVer",
    "io.ktor:ktor-serialization-jackson:$ktorVer"
)

@Suppress("UNUSED_PARAMETER")
private infix fun Unit.and(o: Unit) {
}

private fun DependencyHandler.implementationIf(condition: Boolean, dependencyNotation: Any) {
    if(condition) implementation(dependencyNotation)
}

private fun DependencyHandler.implementation(vararg dependencyNotations: Any) =
    dependencyNotations.forEach { add("implementation", it) }

private fun DependencyHandler.debugImplementation(vararg dependencyNotations: Any) =
    dependencyNotations.forEach { add("debugImplementation", it) }