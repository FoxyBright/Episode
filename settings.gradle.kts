@file:Suppress("UnstableApiUsage")

import java.net.URI

include(":feature:yandexmap")


include(":domain:paginator")


include(":domain")


include(":feature:gallery")


include(":data:chats")


include(":feature:bottomsheet")


include(":data:reports")


include(":data:meetings")


include(":data:profile")


include(":data:notifications")


include(":data:shared")


include(":data:auth")


include(":data:ktor")


include(":data:realm")


include(":data")


include(":feature:animated")


include(":feature:bubbles")


include(":dataexample")


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