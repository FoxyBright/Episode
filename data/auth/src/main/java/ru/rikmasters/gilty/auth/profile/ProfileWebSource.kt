package ru.rikmasters.gilty.auth.profile

import io.ktor.client.call.body
import io.ktor.client.request.get
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL

class ProfileWebSource: KtorSource() { // TODO Сделать нормальный парсер
    suspend fun isUserRegistered(): Boolean = client.get(
        "http://$HOST$PREFIX_URL/profile"
    ).body<String?>()?.let {
        when {
            it.contains("\"status\":\"error\"") -> false
            it.contains("\"username\":null") -> false
            else -> true
        }
    } ?: false
}