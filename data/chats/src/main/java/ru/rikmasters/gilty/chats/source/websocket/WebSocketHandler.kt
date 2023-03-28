package ru.rikmasters.gilty.chats.source.websocket

import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import io.ktor.websocket.Frame
import io.ktor.websocket.send
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import ru.rikmasters.gilty.chats.models.chat.Chat
import ru.rikmasters.gilty.chats.models.ws.*
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType.*
import ru.rikmasters.gilty.chats.models.ws.enums.SocketEvents
import ru.rikmasters.gilty.chats.models.ws.enums.SocketEvents.*
import ru.rikmasters.gilty.chats.repository.ChatRepository
import ru.rikmasters.gilty.chats.repository.MessageRepository
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.meetings.mapper
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.models.User
import java.io.IOException

class WebSocketHandler(
    
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
): KtorSource() {
    
    private val socketURL = "/app/local?protocol=7&client=js&version=7.2.0&flash=false"
    
    private val answer = MutableStateFlow<Pair<AnswerType, Any?>?>(null)
    private val socketId = MutableStateFlow<String?>(null)
    private val chatId = MutableStateFlow<String?>(null)
    
    private suspend fun subscribe(
        channel: String,
        complition: (suspend (Boolean) -> Unit)? = null,
    ) {
        data class Res(val auth: String)
        post(
            "http://$HOST/broadcasting/auth"
        ) {
            setBody(
                mapOf(
                    "socket_id" to socketId.value,
                    "channel_name" to channel
                )
            )
        }?.let { response ->
            complition?.let { it(response.status.isSuccess()) }
            send(
                data = mapOf(
                    "auth" to response.body<Res>().auth,
                    "channel" to channel
                ),
                event = "pusher:subscribe"
            )
        }
    }
    
    suspend fun connectToChat(id: String) {
        disconnectToChat()
        subscribe("private-chats.$id") {
            if(it) chatId.emit(id)
        }
    }
    
    private suspend fun disconnectToChat() {
        chatId.value?.let {
            send(
                data = mapOf("channel" to "private=chats.$it"),
                event = "pusher:unsubscribe"
            )
        }
    }
    
    private suspend fun handle(
        response: SocketResponse,
    ) {
        val event = SocketEvents from response.event
        logV(event?.name.toString())
        when(event) {
            CONNECTION_ESTABLISHED -> {
                
                socketId.emit(mapper.readValue<SocketData>(response.data).socket_id)
                
                subscribe("private-user.${_userId.value}")
            }
            
            SUBSCRIPTION_SUCCEEDED -> {
                logV("$response")
            }
            
            PONG -> inPing = false
            
            CHATS_UPDATED, CHATS_DELETED -> {
                answer.emit(
                    Pair(
                        if(event == CHATS_DELETED)
                            CHAT_DELETED
                        else UPDATED_CHATS,
                        if(event == CHATS_DELETED)
                            mapper.readValue<ShortMessageWs>(
                                response.data
                            ).id
                        else mapper.readValue<Chat>(
                            response.data
                        )
                    )
                )
                chatRepository.chatUpdate(answer.value)
            }
            
            MESSAGE_UPDATE -> {
                answer.emit(
                    Pair(
                        UPDATE_MESSAGE,
                        mapper.readValue<ChatStatus>(
                            response.data
                        ).unreadCount
                    )
                )
            }
            
            CHAT_COMPLETED -> {
                answer.emit(
                    Pair(COMPLETED_CHAT, null)
                )
            }
            
            MESSAGE_SENT, MESSAGE_READ, MESSAGE_DELETED -> {
                answer.emit(
                    Pair(
                        when(event) {
                            MESSAGE_SENT -> NEW_MESSAGE
                            MESSAGE_READ -> READ_MESSAGE
                            else -> DELETE_MESSAGE
                        },
                        if(event == MESSAGE_SENT)
                            mapper.readValue<MessageWs>(response.data).map(chatId.value)
                        else
                            mapper.readValue<ShortMessageWs>(response.data).id
                    )
                )
                logD("INFO MyMessage $response")
                messageRepository.messageUpdate(answer.value)
            }
            
            MESSAGE_TYPING -> {
                val user = mapper.readValue<User>(response.data)
                messageRepository.writersUpdate(
                    UserWs(user.id!!, user.thumbnail!!)
                )
                logD("$response")
            }
            
            else -> {}
        }
    }
    
    private suspend fun send(data: Map<String, String>, event: String) {
        mySession.value?.send(
            mapper.writeValueAsString(
                mapOf(
                    "data" to data,
                    "event" to event
                )
            )
        )
    }
    
    private var inPing: Boolean = false
    
    private suspend fun doPing(session: DefaultClientWebSocketSession) {
        if(inPing) throw IOException("Соединение прервано: не получен PONG.")
        
        val message = mapper.writeValueAsBytes(
            mapOf("event" to "pusher:ping")
        )
        session.send(
            Frame.Text(String(message))
        )
        inPing = true
    }
    
    private val mySession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    private val _userId = MutableStateFlow("")
    private var sessionHandlerJob: Job? = null
    
    fun disconnect() {
        sessionHandlerJob?.cancel()
    }
    
    suspend fun connect(userId: String) = withContext(IO) {
        _userId.emit(userId)
        sessionHandlerJob = launch {
            val session = wsSession(HOST, 6001, socketURL)
            
            try {
                launch {
                    while(true) {
                        delay(20_000)
                        doPing(session)
                    }
                }
                mySession.emit(session)
                while(true) {
                    
                    val response = session.incoming.receive()
                    logV("Frame: ${String(response.data)}")
                    
                    val socketResponse = mapper
                        .readValue<SocketResponse>(response.data)
                    handle(socketResponse)
                }
            } finally {
                logV("Closing session...")
                closeClient()
            }
        }
    }
}