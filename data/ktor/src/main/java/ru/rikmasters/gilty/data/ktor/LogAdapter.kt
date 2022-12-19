package ru.rikmasters.gilty.data.ktor

import io.ktor.client.plugins.logging.Logger
import ru.rikmasters.gilty.core.log.log

object LogAdapter: Logger {
    override fun log(message: String) {
        log.v(message, "KTOR")
    }
}