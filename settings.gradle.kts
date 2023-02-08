@file:Suppress("UnstableApiUsage")

import java.net.URI

include(":data:meetings")


include(":data:profile")


include(":data:push")


include(":data:shared")


include(":data:auth")


include(":data:ktor")


include(":data:realm")


include(":data")


include(":feature:animated")


include(":feature:bubbles")


include(":dataexample")


include(":feature:complaints")


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