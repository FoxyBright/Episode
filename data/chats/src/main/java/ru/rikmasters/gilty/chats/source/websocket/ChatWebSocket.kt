package ru.rikmasters.gilty.chats.source.websocket

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.flow.MutableStateFlow
import ru.rikmasters.gilty.chats.models.chat.Chat
import ru.rikmasters.gilty.chats.models.ws.*
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType.*
import ru.rikmasters.gilty.chats.models.ws.enums.SocketEvents
import ru.rikmasters.gilty.chats.models.ws.enums.SocketEvents.*
import ru.rikmasters.gilty.chats.repository.ChatRepository
import ru.rikmasters.gilty.chats.repository.MessageRepository
import ru.rikmasters.gilty.meetings.mapper
import ru.rikmasters.gilty.shared.models.Thumbnail
import ru.rikmasters.gilty.shared.models.User
import ru.rikmasters.gilty.shared.models.socket.SocketData
import ru.rikmasters.gilty.shared.models.socket.SocketResponse
import ru.rikmasters.gilty.shared.socket.WebSocket

class ChatWebSocket(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
): WebSocket() {
    
    override val port: Int = 6001
    override val pingInterval: Long = 20_000L
    
    override suspend fun handleResponse(response: SocketResponse) {
        val event = SocketEvents from response.event
        logV(event?.name.toString())
        when(event) {
            CONNECTION_ESTABLISHED -> {
                socketId.emit(
                    mapper.readValue<SocketData>(
                        response.data
                    ).socket_id
                )
                subscribe("private-user.${userId.value}")
            }
            
            SUBSCRIPTION_SUCCEEDED -> Unit
            
            PONG -> inPing = false
            
            CHATS_UPDATED, CHATS_DELETED -> {
                answer.emit(
                    Pair(
                        if(event == CHATS_DELETED) {
                            CHAT_DELETED
                        } else {
                            UPDATED_CHATS
                        },
                        if(event == CHATS_DELETED) {
                            mapper.readValue<ShortMessageWs>(
                                response.data,
                            ).id
                        } else {
                            mapper.readValue<Chat>(
                                response.data,
                            )
                        },
                    ),
                )
                chatRepository.chatUpdate(answer.value)
                logD("INFO_MESSAGE $response")
            }
            
            MESSAGE_UPDATE -> {
                answer.emit(
                    Pair(
                        UPDATE_MESSAGE,
                        mapper.readValue<ChatStatus>(
                            response.data,
                        ).unreadCount,
                    ),
                )
                logD("INFO_MESSAGE $response")
                messageRepository.messageUpdate(answer.value)
            }
            
            CHAT_COMPLETED -> {
                answer.emit(
                    Pair(COMPLETED_CHAT, null),
                )
                logD("INFO_MESSAGE $response")
            }
            
            MESSAGE_SENT, MESSAGE_READ, MESSAGE_DELETED -> {
                answer.emit(
                    Pair(
                        when(event) {
                            MESSAGE_SENT -> NEW_MESSAGE
                            MESSAGE_READ -> READ_MESSAGE
                            else -> DELETE_MESSAGE
                        },
                        if(event == MESSAGE_SENT) {
                            mapper.readValue<MessageWs>(response.data)
                                .map(chatId.value)
                        } else {
                            mapper.readValue<ShortMessageWs>(response.data).id
                        },
                    ),
                )
                logD("INFO_MESSAGE $response")
                messageRepository.messageUpdate(answer.value)
            }
            
            MESSAGE_TYPING -> {
                mapper.readValue<User>(response.data).let { user ->
                    if(user.id == messageRepository.getUser()) {
                        return
                    } else {
                        messageRepository.writersUpdate(
                            UserWs(
                                (user.id ?: ""),
                                (user.avatar?.thumbnail ?: Thumbnail()),
                            ),
                        )
                    }
                }
                logD("INFO_MESSAGE $response")
            }
            else -> Unit
        }
    }
    
    private val answer =
        MutableStateFlow<Pair<AnswerType, Any?>?>(null)
    private val chatId =
        MutableStateFlow<String?>(null)
    
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
                event = "pusher:unsubscribe",
            )
        }
    }
}