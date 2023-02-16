package ru.rikmasters.gilty.shared.models.chats

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MessageType

data class Message(
    val id: String,
    val type: String,
    val replied: Message? = null,
    val notification: ChatNotification? = null,
    val message: MemberMessage? = null,
    val other_read: Boolean,
    val isRead: Boolean,
    val createdAt: String,
) {
    
    fun map(isReplied: Boolean = false): MessageModel = MessageModel(
        id, MessageType.valueOf(type),
        if(isReplied) null else replied?.map(true),
        notification?.map(),
        message?.map(), other_read, isRead, isRead, // TODO - must be delivered\read
        of(createdAt).toString()
    )
}