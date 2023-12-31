package ru.rikmasters.gilty.chats.models.chat

import ru.rikmasters.gilty.chats.models.message.Message
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.enumeration.ChatStatus
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType
import ru.rikmasters.gilty.shared.models.User

data class Chat(
    val id: String,
    val meetingId: String,
    val userId: String,
    val meetStatus: String,
    val status: String,
    val isOnline: Boolean,
    val translationStartedAt: String? = null,
    val organizer: User,
    val organizerOnly: Boolean,
    val title: String,
    val lastMessage: Message? = null,
    val datetime: String,
    val unreadCount: Int,
    val canMessage: Boolean,
    val membersCount: Int,
    val createdAt: String,
): DomainEntity {
    
    fun map() = ChatModel(
        id = id,
        meetingId = meetingId,
        userId = userId,
        meetStatus = MeetStatusType.valueOf(meetStatus),
        status = ChatStatus.valueOf(status),
        isOnline = isOnline,
        translationStartedAt = translationStartedAt,
        organizer = organizer.map(),
        organizerOnly = organizerOnly,
        title = title,
        lastMessage = lastMessage?.map(),
        datetime = datetime,
        unreadCount = unreadCount,
        canMessage = canMessage,
        membersCount = membersCount,
        createdAt = "${of(createdAt)}",
    )
    
    override fun primaryKey() = id
}