@file:Suppress("UnstableApiUsage")

import java.net.URI

include(":data:shared")


include(":data:auth")


include(":data:ktor")


include(":data:realm")


include(":data")


include(":feature:animated")


include(":feature:bubbles")


include(":dataexample")


include(":feature:complaints")


include(":feature:settings")


include(":feature:chat")


include(":feature:addmeet")


include(":feature:profile")


include(":feature:notifications")


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
        maven { url = URI("https://jitpack.io") }
    }
}
rootProject.name = "gilty"

include(":app", ":core")

include(":example")

include(":feature")
include(":feature:login")