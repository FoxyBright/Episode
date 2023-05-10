package ru.rikmasters.gilty.translations.datasource.remote

import android.util.Log
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import io.ktor.websocket.Frame
import io.ktor.websocket.send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.shared.BuildConfig
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.models.translations.TranslationSignalDTO
import ru.rikmasters.gilty.shared.socket.SocketData
import ru.rikmasters.gilty.shared.socket.SocketResponse
import ru.rikmasters.gilty.shared.socket.mapper
import ru.rikmasters.gilty.translations.model.TranslationCallbackEvents
import ru.rikmasters.gilty.translations.model.TranslationsSocketEvents
import java.io.IOException
import java.net.SocketException

class TranslationWebSocket : KtorSource() {

    private val socketURL =
        "/app/local?protocol=7&client=js&version=7.2.0&flash=false"

    private val socketId = MutableStateFlow<String?>(null)
    private val translationId = MutableStateFlow<String?>(null)
    private val _userId = MutableStateFlow("")
    private val mySession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    private var sessionHandlerJob: Job? = null

    private val _answer = Channel<TranslationCallbackEvents>()
    val answer = _answer.receiveAsFlow()

    private suspend fun subscribe(
        channel: String,
        completion: (suspend (Boolean) -> Unit)? = null,
    ) {
        data class Res(val auth: String)
        post(
            "http://${BuildConfig.HOST}/broadcasting/auth",
        ) {
            setBody(
                mapOf(
                    "socket_id" to socketId.value,
                    "channel_name" to channel,
                ),
            )
        }?.let { response ->
            completion?.let { it(response.status.isSuccess()) }
            send(
                data = mapOf(
                    "auth" to response.body<Res>().auth,
                    "channel" to channel,
                ),
                event = TranslationsSocketEvents.SUBSCRIBE.value,
            )
        }
    }

    private suspend fun handle(
        response: SocketResponse,
    ) {
        val event = TranslationsSocketEvents from response.event
        logV(event?.name.toString())
        when (event) {
            TranslationsSocketEvents.PONG -> inPing = false
            TranslationsSocketEvents.PING -> {}
            TranslationsSocketEvents.CONNECTION_ESTABLISHED -> {
                socketId.emit(mapper.readValue<SocketData>(response.data).socket_id)
                subscribe("private-translation.${translationId.value}")
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
                val signalDTO = mapper.readValue<TranslationSignalDTO>(response.data)
                _answer.send(
                    TranslationCallbackEvents.SignalReceived(
                        signal = signalDTO,
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
                data class User(val user: String)
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserConnected(
                        user = user.user,
                    ),
                )
            }

            TranslationsSocketEvents.USER_DISCONNECTED -> {
                data class User(val user: String)
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserDisconnected(
                        user = user.user,
                    ),
                )
            }
            TranslationsSocketEvents.USER_KICKED -> {
                data class User(val user: String)
                val user = mapper.readValue<User>(response.data)
                _answer.send(
                    TranslationCallbackEvents.UserKicked(
                        user = user.user,
                    ),
                )
            }
            TranslationsSocketEvents.MESSAGE_SENT -> {
                Log.d("TESTSOCKETS", response.data)
            }
            else -> {}
        }
    }

    private suspend fun send(data: Map<String, String>, event: String) {
        mySession.value?.send(
            mapper.writeValueAsString(
                mapOf(
                    "data" to data,
                    "event" to event,
                ),
            ),
        )
    }

    suspend fun connectToTranslation(id: String) {
        disconnectFromTranslation()
        subscribe("private-translation.$id.$_userId") {
            if (it) translationId.emit(id)
        }
    }

    private suspend fun disconnectFromTranslation() {
        translationId.value?.let {
            send(
                data = mapOf("channel" to "private-translation.$it.$_userId"),
                event = TranslationsSocketEvents.UNSUBSCRIBE.value,
            )
        }
    }

    suspend fun connectToTranslationChat(id: String) {
        disconnectFromTranslationChat()
        subscribe("private-translation.${id}_chat") {
            if (it) translationId.emit(id)
        }
    }

    private suspend fun disconnectFromTranslationChat() {
        translationId.value?.let {
            send(
                data = mapOf("channel" to "private-translation.${it}_chat"),
                event = TranslationsSocketEvents.UNSUBSCRIBE.value,
            )
        }
    }

    private var inPing: Boolean = false

    private suspend fun doPing(session: DefaultClientWebSocketSession) {
        if (inPing) throw IOException("Соединение прервано: не получен PONG.")

        val message = mapper.writeValueAsBytes(
            mapOf("event" to TranslationsSocketEvents.PING.value),
        )
        session.send(
            Frame.Text(String(message)),
        )
        inPing = true
    }

    fun disconnect() {
        sessionHandlerJob?.cancel()
    }

    suspend fun connect(userId: String) {
        try {
            connection(userId)
        } catch (e: Exception) {
            logV("WebSocket Exception: $e")
            logV("Reconnect...")
            disconnect()
            try {
                connection(userId)
            } catch (e: Exception) {
                logE("Bad reconnection")
                logE("$e")
            }
        }
    }

    private suspend fun connection(userId: String) = withContext(Dispatchers.IO) {
        _userId.emit(userId)
        sessionHandlerJob = launch {
            val session = wsSession(BuildConfig.HOST, 6001, socketURL)
            try {
                launch {
                    while (true) {
                        delay(20_000)
                        doPing(session)
                    }
                }
                mySession.emit(session)
                while (true) {
                    val response = session.incoming.receive()
                    logV("Frame: ${String(response.data)}")

                    val socketResponse = mapper
                        .readValue<SocketResponse>(response.data)
                    handle(socketResponse)
                }
            } catch (e: SocketException) {
                e.stackTraceToString()
            } finally {
                logV("Closing session...")
                closeClient()
            }
        }
    }
}
