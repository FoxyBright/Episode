package ru.rikmasters.gilty.translations

import io.ktor.http.HttpStatusCode.Companion.OK
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.common.extentions.toInt
import ru.rikmasters.gilty.shared.wrapper.wrapped
import ru.rikmasters.gilty.translations.models.TranslationInfo
import ru.rikmasters.gilty.translations.models.TranslationMessage
import ru.rikmasters.gilty.translations.models.TranslationMessageAuthor
import ru.rikmasters.gilty.translations.models.TranslationSignalType

class TranslationWebSource : KtorSource() {

    suspend fun getTranslationInfo(translationId: String): TranslationInfo? =
        get("http://$HOST$PREFIX_URL/meetings/$translationId/translation")?.let {
            if (it.status == OK) {
                it.wrapped<TranslationInfo>()
            } else {
                null
            }
        }

    suspend fun endTranslation(translationId: String) {
        post("http://$HOST$PREFIX_URL/translations/$translationId/complete")
    }

    suspend fun sendSignal(
        translationId: String,
        signalType: TranslationSignalType,
        value: Boolean,
    ) {
        post("http://$HOST$PREFIX_URL/translations/$translationId/signal") {
            url {
                query("type" to signalType.name)
                query("value" to value.toInt().toString())
            }
        }
    }

    suspend fun sendMessage(translationId: String, text: String): TranslationMessage? =
        post("http://$HOST$PREFIX_URL/translations/$translationId/message") {
            url {
                query("text" to text)
            }
        }?.let {
            if (it.status == OK) {
                it.wrapped<TranslationMessage>()
            } else {
                null
            }
        }

    suspend fun getMessages(
        translationId: String,
        page: Int?,
        perPage: Int?,
    ): List<TranslationMessage>? =
        get("http://$HOST$PREFIX_URL/translations/$translationId/messages") {
            url {
                page?.let { query("page" to it.toString()) }
                perPage?.let { query("per_page" to it.toString()) }
            }
        }?.let {
            if (it.status == OK) {
                it.wrapped<List<TranslationMessage>>()
            } else {
                null
            }
        }

    suspend fun getConnectedUsers(
        translationId: String,
        query: String,
        page: Int?,
        perPage: Int?
    ): List<TranslationMessageAuthor>? =
        get("http://$HOST$PREFIX_URL/translations/$translationId/connected") {
            url {
                query("query" to query)
                page?.let { query("page" to it.toString()) }
                perPage?.let { query("per_page" to it.toString()) }
            }
        }?.let {
            if (it.status == OK) {
                it.wrapped<List<TranslationMessageAuthor>>()
            } else {
                null
            }
        }

    suspend fun extendTranslation(translationId: String, duration: Long): TranslationInfo? =
        post("http://$HOST$PREFIX_URL/translations/$translationId/extension") {
            url {
                query("duration" to duration.toString())
            }
        }?.let {
            if (it.status == OK) {
                it.wrapped<TranslationInfo>()
            } else {
                null
            }
        }

    suspend fun kickUser(translationId: String, userId: String) {
        post("http://$HOST$PREFIX_URL/translations/$translationId/kick") {
            url {
                query("user_id" to userId)
            }
        }
    }

    suspend fun ping(translationId: String) {
        post("http://$HOST$PREFIX_URL/translations/$translationId/ping")
    }
}
