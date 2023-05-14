package ru.rikmasters.gilty.shared.socket

import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import io.ktor.websocket.Frame
import io.ktor.websocket.send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.models.socket.SocketResponse
import java.io.IOException
import java.net.SocketException

abstract class WebSocketManager : KtorSource() {

    private val socketURL =
        "/app/local?protocol=7&client=js&version=7.2.0&flash=false"
    val socketId = MutableStateFlow<String?>(null)
    private val mySession =
        MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val _userId = MutableStateFlow("")
    private var sessionHandlerJob: Job? = null
    var inPing: Boolean = false

    abstract suspend fun handleResponse(response: SocketResponse)

    suspend fun subscribe(
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
                event = "pusher:subscribe",
            )
        }
    }

    suspend fun send(data: Map<String, String>, event: String) {
        mySession.value?.send(
            mapper.writeValueAsString(
                mapOf(
                    "data" to data,
                    "event" to event,
                ),
            ),
        )
    }

    private suspend fun doPing(session: DefaultClientWebSocketSession) {
        if (inPing) throw IOException("Соединение прервано: не получен PONG.")

        val message = mapper.writeValueAsBytes(
            mapOf("event" to "pusher:ping"),
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
                    handleResponse(socketResponse)
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