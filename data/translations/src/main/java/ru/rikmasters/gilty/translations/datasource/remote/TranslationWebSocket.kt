package ru.rikmasters.gilty.translations.datasource.remote

import android.util.Log
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.translations.TranslationSignalModel
import ru.rikmasters.gilty.shared.models.socket.SocketData
import ru.rikmasters.gilty.shared.models.socket.SocketResponse
import ru.rikmasters.gilty.shared.socket.WebSocket
import ru.rikmasters.gilty.shared.socket.mapper
import ru.rikmasters.gilty.translations.model.TranslationCallbackEvents
import ru.rikmasters.gilty.translations.model.TranslationsSocketEvents

data class User(val user: String, val count: String)
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
                        user = user.user,
                        count = user.count.toInt()
                    ),
                )
            }

            TranslationsSocketEvents.USER_DISCONNECTED -> {
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserDisconnected(
                        user = user.user,
                        count = user.count.toInt()
                    ),
                )
            }
            TranslationsSocketEvents.USER_KICKED -> {
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserKicked(
                        user = user.user,
                        count = user.count.toInt()
                    ),
                )
            }
            TranslationsSocketEvents.MESSAGE_SENT -> {
                Log.d("TESTSOCKETS", response.data)
            }
            else -> {}
        }
    }

    private val _translationId = MutableStateFlow<String?>(null)

    private val _answer = Channel<TranslationCallbackEvents>()
    val answer = _answer.receiveAsFlow()

    suspend fun connectToTranslation(id: String) {
        disconnectFromTranslation()
        Log.d("TranslationWeb","CONNECT TRY WITH USERID ${_userId.value} TRRRID $id")
        subscribe("private-translation.$id.${_userId.value}") {
            if (it) _translationId.emit(id)
        }
    }

    suspend fun disconnectFromTranslation() {
        _translationId.value?.let {
            send(
                data = mapOf("channel" to "private-translation.$it.${_userId.value}"),
                event = TranslationsSocketEvents.UNSUBSCRIBE.value,
            )
        }
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