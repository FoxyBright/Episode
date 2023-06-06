package ru.rikmasters.gilty.translations.datasource.remote

import android.util.Log
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.DataStateTest
import ru.rikmasters.gilty.shared.model.ExceptionCause
import ru.rikmasters.gilty.shared.model.translations.TranslationSignalModel
import ru.rikmasters.gilty.shared.models.User
import ru.rikmasters.gilty.shared.models.socket.SocketData
import ru.rikmasters.gilty.shared.models.socket.SocketResponse
import ru.rikmasters.gilty.shared.socket.WebSocket
import ru.rikmasters.gilty.shared.socket.mapper
import ru.rikmasters.gilty.translations.model.TranslationCallbackEvents
import ru.rikmasters.gilty.translations.model.TranslationsSocketEvents

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
                _answer.send(TranslationCallbackEvents.ConnectionEstablished)
            }

            TranslationsSocketEvents.SUBSCRIPTION_SUCCEEDED -> {
                logV("$response")
            }

            TranslationsSocketEvents.SUBSCRIBE -> {
            }

            TranslationsSocketEvents.UNSUBSCRIBE -> {}
            TranslationsSocketEvents.TRANSLATION_STARTED -> {
                _answer.send(TranslationCallbackEvents.TranslationStarted)
            }

            TranslationsSocketEvents.TRANSLATION_COMPLETED -> {
                _answer.send(TranslationCallbackEvents.TranslationCompleted)
            }

            TranslationsSocketEvents.SIGNAL -> {
                val signal = mapper.readValue<TranslationSignalModel>(response.data)
                _answer.send(
                    TranslationCallbackEvents.SignalReceived(
                        signal = signal,
                    ),
                )
            }

            TranslationsSocketEvents.APPEND_TIME -> {
                data class AppendInfo(val completed_at: String, val duration: String)

                val info = mapper.readValue<AppendInfo>(response.data)
                _answer.send(
                    TranslationCallbackEvents.TranslationExtended(
                        completedAt = LocalDateTime.of(info.completed_at),
                        duration = info.duration.toInt(),
                    ),
                )
            }

            TranslationsSocketEvents.EXPIRED -> {
                _answer.send(TranslationCallbackEvents.TranslationExpired)
            }

            TranslationsSocketEvents.USER_CONNECTED -> {
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserConnected(
                        user = user.map()
                    ),
                )
            }

            TranslationsSocketEvents.USER_DISCONNECTED -> {
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserDisconnected(
                        user = user.map()
                    ),
                )
            }

            TranslationsSocketEvents.USER_KICKED -> {
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserKicked(
                        user = user.map()
                    ),
                )
            }

            TranslationsSocketEvents.MESSAGE_SENT -> {
                _answer.send(
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
            _answer.send(
                TranslationCallbackEvents.MembersCountChanged(
                    count = count.count
                )
            )
        }
    }

    private val _translationId = MutableStateFlow<String?>(null)

    private val _answer = Channel<TranslationCallbackEvents>()
    val answer = _answer.receiveAsFlow()

    suspend fun connectToTranslation(id: String) {
        disconnectFromTranslation()
        subscribe("private-translation.$id.${_userId.value}") {
            if (it) _translationId.emit(id)
        }
    }

    suspend fun disconnectFromTranslation(): DataStateTest<Unit> {
        return _translationId.value?.let {
            trySend(
                data = mapOf("channel" to "private-translation.$it.${_userId.value}"),
                event = TranslationsSocketEvents.UNSUBSCRIBE.value,
            )
        } ?: DataStateTest.Error(
            cause = ExceptionCause.IO
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