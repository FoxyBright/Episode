package ru.rikmasters.gilty.translations.datasource.remote

import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.models.FullUserDTO
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.common.extentions.toInt
import ru.rikmasters.gilty.shared.models.enumeration.TranslationSignalTypeDTO
import ru.rikmasters.gilty.shared.models.translations.TranslationInfoDTO
import ru.rikmasters.gilty.shared.models.translations.TranslationMessageDTO
import ru.rikmasters.gilty.shared.wrapper.wrapped
import ru.rikmasters.gilty.shared.wrapper.wrappedTest

class TranslationWebSource : KtorSource() {

    suspend fun getTranslationInfo(translationId: String) =
        tryGet("http://$HOST$PREFIX_URL/meetings/$translationId/translation").wrappedTest<TranslationInfoDTO>()

    suspend fun endTranslation(translationId: String) =
        tryPost("http://$HOST$PREFIX_URL/translations/$translationId/complete")

    suspend fun sendSignal(
        translationId: String,
        signalType: TranslationSignalTypeDTO,
        value: Boolean,
    ) = tryPost("http://$HOST$PREFIX_URL/translations/$translationId/signal") {
        url {
            query("type" to signalType.name)
            query("value" to value.toInt().toString())
        }
    }

    suspend fun sendMessage(translationId: String, text: String): TranslationMessageDTO =
        tryPost("http://$HOST$PREFIX_URL/translations/$translationId/message") {
            url {
                query("text" to text)
            }
        }.wrapped()

    suspend fun getMessages(
        translationId: String,
        page: Int?,
        perPage: Int?,
    ): List<TranslationMessageDTO> =
        tryGet("http://$HOST$PREFIX_URL/translations/$translationId/messages") {
            url {
                page?.let { query("page" to it.toString()) }
                perPage?.let { query("per_page" to it.toString()) }
            }
        }.wrapped()

    suspend fun getConnectedUsers(
        translationId: String,
        query: String?,
        page: Int?,
        perPage: Int?,
    ): List<FullUserDTO> =
        tryGet("http://$HOST$PREFIX_URL/translations/$translationId/connected") {
            url {
                query?.let { query("query" to query) }
                page?.let { query("page" to it.toString()) }
                perPage?.let { query("per_page" to it.toString()) }
            }
        }.wrapped()

    suspend fun extendTranslation(translationId: String, duration: Long): TranslationInfoDTO =
        tryPost("http://$HOST$PREFIX_URL/translations/$translationId/extension") {
            url {
                query("duration" to duration.toString())
            }
        }.wrapped()

    suspend fun kickUser(translationId: String, userId: String) =
        tryPost("http://$HOST$PREFIX_URL/translations/$translationId/kick") {
            url {
                query("user_id" to userId)
            }
        }

    suspend fun ping(translationId: String) =
        tryPost("http://$HOST$PREFIX_URL/translations/$translationId/ping")
}