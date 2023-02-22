package ru.rikmasters.gilty.chats.websocket

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.websocket.Frame
import io.ktor.websocket.send
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import ru.rikmasters.gilty.chats.repository.ChatRepository
import ru.rikmasters.gilty.chats.websocket.enums.AnswerType
import ru.rikmasters.gilty.chats.websocket.enums.AnswerType.*
import ru.rikmasters.gilty.chats.websocket.enums.SocketEvents
import ru.rikmasters.gilty.chats.websocket.enums.SocketEvents.*
import ru.rikmasters.gilty.chats.websocket.model.ChatStatus
import ru.rikmasters.gilty.chats.websocket.model.SocketData
import ru.rikmasters.gilty.chats.websocket.model.SocketResponse
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.models.User
import ru.rikmasters.gilty.shared.models.chats.Chat
import ru.rikmasters.gilty.shared.models.chats.Message
import java.io.IOException

class WebSocketHandler(
    
    private val chatRepository: ChatRepository,
): KtorSource() {
    
    private val socketURL = "/app/local?protocol=7&client=js&version=7.2.0&flash=false"
    
    private val mapper = jsonMapper {
        propertyNamingStrategy(PropertyNamingStrategies.SnakeCaseStrategy())
        addModule(kotlinModule())
    }
    
    private val answer = MutableStateFlow<Pair<AnswerType, Any?>?>(null)
    
    private suspend fun handle(
        response: SocketResponse,
    ) {
        val event = SocketEvents from response.event
        logV(event?.name.toString())
        when(event) {
            CONNECTION_ESTABLISHED -> {
                
                data class Res(val auth: String)
                
                val body = mapOf(
                    "socket_id" to mapper.readValue<SocketData>(response.data).socket_id,
                    "channel_name" to "private-user.${_userId.value}"
                )
                
                updateClientToken()
                val auth = client.post(
                    "http://$HOST/broadcasting/auth"
                ) { setBody(body) }.body<Res>().auth
                
                send(
                    mapper.writeValueAsString(
                        mapOf(
                            "data" to mapOf(
                                "auth" to auth,
                                "channel" to "private-user.${_userId.value}"
                            ),
                            "event" to "pusher:subscribe"
                        )
                    )
                )
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
                        mapper.readValue<Chat>(
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
                        }, mapper.readValue<Message>(
                            response.data
                        ).map()
                    )
                )
            }
            
            MESSAGE_TYPING -> {
                answer.emit(
                    Pair(
                        TYPING_MESSAGE,
                        mapper.readValue<User>(
                            response.data
                        ).map()
                    )
                )
            }
            
            else -> {}
        }
    }
    
    private suspend fun send(data: String) {
        logD("data:\t$data")
        mySession.value?.send(data)
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
    
    suspend fun connect(userId: String) = withContext(IO) {
        _userId.emit(userId)
        sessionHandlerJob = launch {
            val session = unauthorizedClient
                .webSocketSession(host = HOST, port = 6001, path = socketURL)
            
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
                client.close()
            }
            
        }
    }
}