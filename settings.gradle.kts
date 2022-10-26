@file:Suppress("UnstableApiUsage")

include(":feature:mainscreen")


include(":shared")



pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "gilty"

include(":app", ":core")

include(":example")

include(":feature")
include(":feature:login")