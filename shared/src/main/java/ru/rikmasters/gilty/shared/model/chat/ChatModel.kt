package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.common.extentions.NOW_DATE
import ru.rikmasters.gilty.shared.common.extentions.TOMORROW
import ru.rikmasters.gilty.shared.common.extentions.YESTERDAY
import ru.rikmasters.gilty.shared.model.enumeration.ChatStatus
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoUserModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel

data class ChatModel(
    val id: String,
    val meetingId: String,
    val userId: String,
    val meetStatus: MeetStatusType,
    val status: ChatStatus,
    val isOnline: Boolean,
    val translationStartedAt: String? = null,
    val organizer: UserModel,
    val organizerOnly: Boolean,
    val title: String,
    val lastMessage: MessageModel,
    val datetime: String,
    val unreadCount: Int,
    val canMessage: Boolean,
    val memberCount: Int,
    val createdAt: String,
)

val DemoChatModel = ChatModel(
    "1",
    "1",
    "1",
    MeetStatusType.ACTIVE,
    ChatStatus.ACTIVE,
    false,
    null,
    DemoUserModel,
    false,
    DemoMeetingModel.title,
    DemoMessageModel,
    "2022-11-28T20:00:54.140Z",
    0,
    true,
    DemoMeetingModel.memberCount,
    "2022-11-28T20:00:54.140Z"
)

val DemoChatModelList = listOf(
    DemoChatModel.copy(id = "1", datetime = NOW_DATE, isOnline = true, unreadCount = 10),
    DemoChatModel.copy(id = "2", datetime = TOMORROW),
    DemoChatModel.copy(id = "3", datetime = TOMORROW, unreadCount = 4),
    DemoChatModel.copy(id = "4", datetime = YESTERDAY, isOnline = true),
    DemoChatModel.copy(id = "5", datetime = YESTERDAY)
)