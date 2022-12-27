package ru.rikmasters.gilty.core.data.source

abstract class WebSource: Source {
    companion object {
        const val ENV_HOST = "WEB_HOST"
        const val ENV_USER_AGENT = "WEB_USER_AGENT"
    }
}