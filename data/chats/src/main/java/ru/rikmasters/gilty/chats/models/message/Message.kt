package ru.rikmasters.gilty.chats.models.message

import ru.rikmasters.gilty.chats.models.chat.ChatNotification
import ru.rikmasters.gilty.chats.paging.messages.entity.DBMessage
import ru.rikmasters.gilty.chats.paging.messages.objToJSON
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.valueOf

data class Message(
    val id: String,
    val type: String,
    val replied: Message? = null,
    val notification: ChatNotification? = null,
    val message: MemberMessage? = null,
    val otherRead: Boolean,
    val isRead: Boolean,
    val createdAt: String,
    val chatId: String? = null,
): DomainEntity {
    
    fun mapToBase() = DBMessage(
        id = id,
        type = type,
        replied = objToJSON(replied),
        notification = objToJSON(notification),
        message = objToJSON(message),
        otherRead = otherRead,
        isRead = isRead,
        createdAt = createdAt,
        chatId = chatId,
    )
    
    fun map(): MessageModel = MessageModel(
        id = id,
        type = valueOf(type),
        replied = replied?.map(),
        notification = notification?.map(),
        message = message?.map(),
        otherRead = otherRead,
        isRead = isRead,
        createdAt = of(createdAt).toString()
    )
    
    override fun primaryKey() = id
}