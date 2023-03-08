package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.common.extentions.NOW_DATE
import ru.rikmasters.gilty.shared.common.extentions.TOMORROW
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
    val lastMessage: MessageModel? = null,
    val datetime: String,
    val unreadCount: Int,
    val canMessage: Boolean,
    val membersCount: Int,
    val createdAt: String,
) {
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + meetingId.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + meetStatus.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + isOnline.hashCode()
        result = 31 * result + (translationStartedAt?.hashCode() ?: 0)
        result = 31 * result + organizer.hashCode()
        result = 31 * result + organizerOnly.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + lastMessage.hashCode()
        result = 31 * result + datetime.hashCode()
        result = 31 * result + unreadCount
        result = 31 * result + canMessage.hashCode()
        result = 31 * result + membersCount
        result = 31 * result + createdAt.hashCode()
        return result
    }
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false
        
        other as ChatModel
        
        if(id != other.id) return false
        if(meetingId != other.meetingId) return false
        if(userId != other.userId) return false
        if(meetStatus != other.meetStatus) return false
        if(status != other.status) return false
        if(isOnline != other.isOnline) return false
        if(translationStartedAt != other.translationStartedAt) return false
        if(organizer != other.organizer) return false
        if(organizerOnly != other.organizerOnly) return false
        if(title != other.title) return false
        if(lastMessage != other.lastMessage) return false
        if(datetime != other.datetime) return false
        if(unreadCount != other.unreadCount) return false
        if(canMessage != other.canMessage) return false
        if(membersCount != other.membersCount) return false
        if(createdAt != other.createdAt) return false
        
        return true
    }
}

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
    DemoChatModel.copy(
        id = "4",
        status = ChatStatus.COMPLETED,
        isOnline = true,
        unreadCount = 1412
    ),
    DemoChatModel.copy(id = "5", status = ChatStatus.COMPLETED)
)