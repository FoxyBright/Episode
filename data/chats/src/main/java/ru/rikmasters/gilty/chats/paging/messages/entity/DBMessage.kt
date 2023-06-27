package ru.rikmasters.gilty.chats.paging.messages.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.rikmasters.gilty.chats.models.chat.ChatNotification
import ru.rikmasters.gilty.chats.models.message.MemberMessage
import ru.rikmasters.gilty.chats.models.message.Message
import ru.rikmasters.gilty.chats.paging.messages.objFromJSON
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MessageType

@Entity(tableName = "messages")
data class DBMessage(
    @PrimaryKey
    val id: String,
    val type: String,
    val replied: String? = null,
    val notification: String? = null,
    val message: String? = null,
    val otherRead: Boolean,
    val isRead: Boolean,
    val createdAt: String,
    val chatId: String? = null,
) {
    
    fun map(): MessageModel = MessageModel(
        id = id,
        type = MessageType.valueOf(type),
        replied = replied?.let {
            objFromJSON<Message>(it)?.map()
        },
        notification = notification?.let {
            objFromJSON<ChatNotification>(it)?.map()
        },
        message = message?.let {
            objFromJSON<MemberMessage>(it)?.map()
        },
        otherRead = otherRead,
        isRead = isRead,
        createdAt = of(createdAt).toString()
    )
}