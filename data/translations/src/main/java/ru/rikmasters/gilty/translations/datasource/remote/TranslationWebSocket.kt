package ru.rikmasters.gilty.translations.datasource.remote

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.rikmasters.gilty.shared.model.DataStateTest
import ru.rikmasters.gilty.shared.model.ExceptionCause
import ru.rikmasters.gilty.shared.model.translations.TranslationSignalModel
import ru.rikmasters.gilty.shared.models.socket.SocketData
import ru.rikmasters.gilty.shared.models.socket.SocketResponse
import ru.rikmasters.gilty.shared.models.translations.AppendInfo
import ru.rikmasters.gilty.shared.models.translations.TranslationUser
import ru.rikmasters.gilty.shared.socket.WebSocket
import ru.rikmasters.gilty.shared.socket.mapper
import ru.rikmasters.gilty.translations.model.TranslationCallbackEvents
import ru.rikmasters.gilty.translations.model.TranslationsSocketEvents
import java.time.ZoneId
import java.time.ZonedDateTime

data class Count(val count: Int)
class TranslationWebSocket : WebSocket() {

    override val port: Int = 6002

    override val pingInterval: Long = 10_000

    fun updateTranslationId(translationId: String) {
        _translationId.value = translationId
    }

    override suspend fun handleResponse(response: SocketResponse) {
        val event = TranslationsSocketEvents from response.event
        logV(event?.name.toString())
        when (event) {
            TranslationsSocketEvents.PONG -> inPing = false
            TranslationsSocketEvents.PING -> {}
            TranslationsSocketEvents.CONNECTION_ESTABLISHED -> {
                socketId.emit(mapper.readValue<SocketData>(response.data).socket_id)
                subscribe("private-translation.${_translationId.value}")
                _answer.emit(TranslationCallbackEvents.ConnectionEstablished)
            }

            TranslationsSocketEvents.SUBSCRIPTION_SUCCEEDED -> {
                logV("$response")
            }

            TranslationsSocketEvents.SUBSCRIBE -> {
            }

            TranslationsSocketEvents.UNSUBSCRIBE -> {}
            TranslationsSocketEvents.TRANSLATION_STARTED -> {
                _answer.emit(TranslationCallbackEvents.TranslationStarted)
            }

            TranslationsSocketEvents.TRANSLATION_COMPLETED -> {
                _answer.emit(TranslationCallbackEvents.TranslationCompleted)
            }

            TranslationsSocketEvents.SIGNAL -> {
                val signal = mapper.readValue<TranslationSignalModel>(response.data)
                _answer.emit(
                    TranslationCallbackEvents.SignalReceived(
                        signal = signal,
                    ),
                )
            }

            TranslationsSocketEvents.APPEND_TIME -> {
                val info = mapper.readValue<AppendInfo>(response.data)
                _answer.emit(
                    TranslationCallbackEvents.TranslationExtended(
                        completedAt = ZonedDateTime.parse(info.completed_at).withZoneSameInstant(
                            ZoneId.of("Europe/Moscow")).withZoneSameLocal(
                            ZoneId.systemDefault()),
                        duration = info.duration.toInt(),
                    )
                )
            }

            TranslationsSocketEvents.EXPIRED -> {
                _answer.emit(TranslationCallbackEvents.TranslationExpired)
            }

            TranslationsSocketEvents.USER_CONNECTED -> {
                val user = mapper.readValue<TranslationUser>(response.data)
                _answer.emit(
                    TranslationCallbackEvents.UserConnected(
                        user = user.user.map()
                    ),
                )
            }

            TranslationsSocketEvents.USER_DISCONNECTED -> {
                val user = mapper.readValue<TranslationUser>(response.data)
                _answer.emit(
                    TranslationCallbackEvents.UserDisconnected(
                        user = user.user.map()
                    ),
                )
            }

            TranslationsSocketEvents.USER_KICKED -> {
                val user = mapper.readValue<TranslationUser>(response.data)
                _answer.emit(
                    TranslationCallbackEvents.UserKicked(
                        user = user.user.map()
                    ),
                )
            }

            TranslationsSocketEvents.MESSAGE_SENT -> {
                _answer.emit(
                    TranslationCallbackEvents.MessageReceived
                )
            }

            else -> {}
        }
        if (
            event == TranslationsSocketEvents.USER_CONNECTED || event == TranslationsSocketEvents.USER_DISCONNECTED
            || event == TranslationsSocketEvents.USER_KICKED
        ) {
            val count = mapper.readValue<Count>(response.data)
            _answer.emit(
                TranslationCallbackEvents.MembersCountChanged(
                    count = count.count
                )
            )
        }
    }

    private val _translationId = MutableStateFlow<String?>(null)

    private val _answer = MutableSharedFlow<TranslationCallbackEvents>(1,0, BufferOverflow.DROP_OLDEST)
    val answer = _answer.asSharedFlow()

    suspend fun connectToTranslation(id: String) {
        subscribe("private-translation.$id.${userId.value}") {
            if (it) _translationId.emit(id)
        }
    }

    suspend fun disconnectFromTranslation(): DataStateTest<Unit> {
        return _translationId.value?.let {
            trySend(
                data = mapOf("channel" to "private-translation.$it.${userId.value}"),
                event = TranslationsSocketEvents.UNSUBSCRIBE.value,
            )
        } ?: DataStateTest.Error(
            cause = ExceptionCause.IO("null")
        )
    }

    suspend fun connectToTranslationChat(id: String) {
        disconnectFromTranslationChat()
        subscribe("private-translation.${id}_chat") {
            if (it) _translationId.emit(id)
        }
    }

    suspend fun disconnectFromTranslationChat() {
        _translationId.value?.let {
            send(
                data = mapOf("channel" to "private-translation.${it}_chat"),
                event = TranslationsSocketEvents.UNSUBSCRIBE.value,
            )
        }
    }
}