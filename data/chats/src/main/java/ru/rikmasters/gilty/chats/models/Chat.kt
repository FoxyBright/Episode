package ru.rikmasters.gilty.chats.models

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
    val lastMessage: Message,
    val datetime: String,
    val unreadCount: Int,
    val canMessage: Boolean,
    val membersCount: Int,
    val createdAt: String,
): DomainEntity {
    
    fun map() = ChatModel(
        id, meetingId, userId,
        MeetStatusType.valueOf(meetStatus),
        ChatStatus.valueOf(status),
        isOnline, translationStartedAt,
        organizer.map(), organizerOnly,
        title, lastMessage.map(),
        datetime, unreadCount, canMessage,
        membersCount, ("${of(createdAt)}"),
    )
    
    override fun primaryKey() = id
}