package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.enumeration.NotificationStatus
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import java.util.UUID

data class ShortNotification(
    val id: String,
    val name: String,
    val description: String,
)

data class NotificationModel(
    val id: String,
    val date: String,
    val type: NotificationType,
    val status: NotificationStatus,
    val parent: NotificationParentModel,
    val feedback: FeedBackModel?,
) {
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + parent.hashCode()
        result = 31 * result + (feedback?.hashCode() ?: 0)
        return result
    }
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false
        
        other as NotificationModel
        
        return when {
            id != other.id -> false
            date != other.date -> false
            type != other.type -> false
            status != other.status -> false
            parent != other.parent -> false
            feedback != other.feedback -> false
            else -> true
        }
    }
}

val DemoShortNotification = ShortNotification(
    UUID.randomUUID().toString(),
    "NOTIFICATION",
    "DESCRIPTION"
)

val DemoNotificationMeetingOverModel = NotificationModel(
    UUID.randomUUID().toString(),
    "2022-11-07T08:35:54.140Z",
    NotificationType.MEETING_OVER,
    NotificationStatus.DELETED,
    DemoNotificationParentModel,
    DemoFeedBackModel
)

val DemoNotificationRespondAccept = NotificationModel(
    UUID.randomUUID().toString(),
    "2022-11-07T08:35:54.140Z",
    NotificationType.RESPOND_ACCEPTED,
    NotificationStatus.DELETED,
    DemoNotificationParentModel,
    DemoFeedBackModel
)

val DemoNotificationModelList = listOf(
    DemoNotificationMeetingOverModel,
    DemoNotificationRespondAccept,
)