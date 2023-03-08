package ru.rikmasters.gilty.chats.models.chat

import ru.rikmasters.gilty.shared.model.chat.ChatNotificationModel
import ru.rikmasters.gilty.shared.model.enumeration.ChatNotificationType
import ru.rikmasters.gilty.shared.models.User

data class ChatNotification(
    val type: String,
    val member: User? = null,
) {
    
    fun map() = ChatNotificationModel(
        ChatNotificationType.valueOf(type),
        member?.map()
    )
}
