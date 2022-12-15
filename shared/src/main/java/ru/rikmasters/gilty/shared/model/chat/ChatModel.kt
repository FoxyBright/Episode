package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.common.extentions.NOW_DATE
import ru.rikmasters.gilty.shared.common.extentions.TOMORROW
import ru.rikmasters.gilty.shared.common.extentions.YESTERDAY
import ru.rikmasters.gilty.shared.model.meeting.*

enum class ChatStatus { ACTIVE, INACTIVE }

data class ChatModel(
    val id: String,
    val meetingId: String,
    val userId: String,
    val meetStatus: MeetStatus,
    val status: ChatStatus,
    val isOnline: Boolean,
    val organizer: OrganizerModel,
    val organizerOnly: Boolean,
    val title: String,
    val lastMessage: MessageModel,
    val dateTime: String,
    val hasUnread: Boolean,
    val canMessage: Boolean,
    val memberCount: Int,
    val createdAt: String
)

fun getChatWithData(
    id: String = "1",
    meetingId: String = "1",
    userId: String = "1",
    meetStatus: MeetStatus = MeetStatus.ACTIVE,
    status: ChatStatus = ChatStatus.ACTIVE,
    isOnline: Boolean = false,
    organizer: OrganizerModel = DemoOrganizerModel,
    organizerOnly: Boolean = false,
    title: String = DemoFullMeetingModel.title,
    lastMessage: MessageModel = DemoMessageModel,
    dateTime: String = "2022-11-28T20:00:54.140Z",
    hasUnread: Boolean = false,
    canMessage: Boolean = false,
    memberCount: Int = DemoFullMeetingModel.memberCount,
    createdAt: String = "2022-11-28T20:00:54.140Z"
) = ChatModel(
    id, meetingId, userId, meetStatus,
    status, isOnline, organizer,
    organizerOnly, title,
    lastMessage, dateTime,
    hasUnread, canMessage,
    memberCount, createdAt
)

val DemoChatListModel = listOf(
    getChatWithData(id = "1", dateTime = NOW_DATE, isOnline = true, hasUnread = true),
    getChatWithData(id = "2", dateTime = TOMORROW),
    getChatWithData(id = "3", dateTime = TOMORROW, hasUnread = true),
    getChatWithData(id = "4", dateTime = YESTERDAY, isOnline = true),
    getChatWithData(id = "5", dateTime = YESTERDAY)
)