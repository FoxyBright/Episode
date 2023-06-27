package ru.rikmasters.gilty.chats.models.ws

import ru.rikmasters.gilty.chats.models.chat.ChatNotification
import ru.rikmasters.gilty.chats.models.message.MemberMessage
import ru.rikmasters.gilty.chats.models.message.Message

data class MessageWs(
    val id: String,
    val type: String,
    val replied: MessageWs? = null,
    val notification: ChatNotification? = null,
    val message: MemberMessage? = null,
    val createdAt: String,
) {
    
    fun map(chatId: String?): Message = Message(
        id = id,
        type = type,
        replied = replied?.map(chatId),
        notification = notification,
        message,
        otherRead = false,
        isRead = false,
        createdAt = createdAt,
        chatId = chatId
    )
}

data class ShortMessageWs(
    val id: String,
)