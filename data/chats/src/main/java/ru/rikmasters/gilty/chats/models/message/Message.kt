package ru.rikmasters.gilty.chats.models.message

import ru.rikmasters.gilty.chats.models.chat.ChatNotification
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MessageType

data class Message(
    val id: String,
    val type: String,
    val replied: Message? = null,
    val notification: ChatNotification? = null,
    val message: MemberMessage? = null,
    val otherRead: Boolean,
    val isRead: Boolean,
    val isDelivered: Boolean? = null,
    val createdAt: String,
    val chatId: String? = null,
): DomainEntity {
    
    fun map(): MessageModel = MessageModel(
        id = id,
        type = MessageType.valueOf(type),
        replied = replied?.map(),
        notification = notification?.map(),
        message = message?.map(),
        otherRead = otherRead,
        isRead = isRead,
        isDelivered = isDelivered == true,
        createdAt = LocalDateTime.of(createdAt).toString()
    )
    
    override fun primaryKey() = id
}