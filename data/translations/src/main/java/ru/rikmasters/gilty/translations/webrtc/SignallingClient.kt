package ru.rikmasters.gilty.translations.webrtc

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import org.webrtc.SessionDescription
import ru.rikmasters.gilty.translations.webrtc.model.signalling.AnswerMessage
import ru.rikmasters.gilty.translations.webrtc.model.signalling.Offer
import ru.rikmasters.gilty.translations.webrtc.model.signalling.SdpAnswer
import ru.rikmasters.gilty.translations.webrtc.model.signalling.SignalingCommand
import ru.rikmasters.gilty.translations.webrtc.model.signalling.mapToMessage
import ru.rikmasters.gilty.translations.webrtc.model.signalling.mapToOffer
import ru.rikmasters.gilty.translations.webrtc.model.signalling.toJsonString
import ru.rikmasters.gilty.translations.webrtc.utils.toMap


class SignalingClient {

    var isConnection = false
        set(value) {
            field = value
            signalingScope.launch {
                _signalingEventFlow.emit(
                    WebRTCClientEvent.Connection(
                        isConnected = field
                    )
                )
            }
        }

    private val signalingScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val client = OkHttpClient()

    private var request: Request? = null
    // Web socket connect with signalling server
    private var ws: WebSocket? = null

    private var id: Int = 0

    fun startConnection(wsUrl: String) {
        request = Request.Builder()
            .url(wsUrl)
            .build()
        ws = client.newWebSocket(request!!, SignalingWebSocketListener())
    }

    // signaling commands to send commands to value pairs to the subscribers
    private val _signalingEventFlow = MutableSharedFlow<WebRTCClientEvent>()
    val signalingEventFlow: SharedFlow<WebRTCClientEvent> = _signalingEventFlow

    // Send command to webSocket
    private fun sendCommand(message: AnswerMessage) {
        if (isConnection) {
            val gson = Gson()
            val jsonSendString = gson.toJson(message).toString()
            ws?.send(jsonSendString)
        }
    }

    private fun sendRequestOffer() {
        val map = mapOf(
            "command" to "request_offer"
        )
        val jsonSendString = JSONObject(map).toString()
        ws?.send(jsonSendString)
    }

    private fun requestOffer() {
        sendRequestOffer()
    }

    fun destroy() {
        isConnection = false
        signalingScope.cancel()
        ws?.cancel()
    }

    fun connection() {
        isConnection = true
        requestOffer()
    }

    fun answer(sdp: SessionDescription) {
        sendCommand(
            AnswerMessage(
                command = SignalingCommand.ANSWER.toJsonString(),
                id = id,
                sdp = SdpAnswer(
                    sdp = sdp.description
                )
            )
        )
    }

    private fun handleSocketMessage(message: String) {
        val data = String(message.toByteArray(), Charsets.UTF_8)
        val jsonObject = JSONObject(data)
        val map = jsonObject.toMap()
        val signallingMessage = map.mapToMessage()
        when(signallingMessage.command) {
            SignalingCommand.OFFER -> {
                val offer = signallingMessage.body.mapToOffer()
                id = offer.id
                signalingScope.launch {
                    _signalingEventFlow.emit(
                        WebRTCClientEvent.OfferSent(
                            offer = offer
                        )
                    )
                }
            }
            else -> {}
        }
    }



    private inner class SignalingWebSocketListener : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            handleSocketMessage(text)
        }
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            destroy()
        }
        override fun onOpen(webSocket: WebSocket, response: Response) {
            connection()
        }
        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            destroy()
        }
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            destroy()
        }
    }
}

sealed interface WebRTCClientEvent {
    data class Connection(val isConnected: Boolean) : WebRTCClientEvent
    data class OfferSent(val offer: Offer) : WebRTCClientEvent
}
