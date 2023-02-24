package ru.rikmasters.gilty.chats.models

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
        isDelivered = true,
        createdAt = createdAt,
        chatId = chatId
    )
}

data class ShortMessageWs(
    val id: String,
)