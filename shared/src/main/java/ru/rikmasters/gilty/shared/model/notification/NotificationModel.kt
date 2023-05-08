package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.common.extentions.NOW_DATE
import ru.rikmasters.gilty.shared.model.enumeration.NotificationStatus
import ru.rikmasters.gilty.shared.model.enumeration.NotificationStatus.DELETED
import ru.rikmasters.gilty.shared.model.enumeration.NotificationStatus.NEW
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.ADMIN_NOTIFICATION
import java.util.UUID.randomUUID

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
    
    @Suppress("unused")
    constructor(): this(
        randomUUID().toString(),
        NOW_DATE, ADMIN_NOTIFICATION, DELETED,
        NotificationParentModel(), (null)
    )
    
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
    randomUUID().toString(),
    "NOTIFICATION",
    "DESCRIPTION"
)

val DemoInfoNotificationModel = NotificationModel(
    randomUUID().toString(),
    "2022-11-07T08:35:54.140Z",
    ADMIN_NOTIFICATION, NEW,
    DemoNotificationParentModel,
    DemoFeedBackModel
)

val DemoNotificationMeetingOverModel = NotificationModel(
    randomUUID().toString(),
    "2022-11-07T08:35:54.140Z",
    NotificationType.MEETING_OVER,
    DELETED,
    DemoNotificationParentModel,
    DemoFeedBackModel
)

val DemoNotificationRespondAccept = NotificationModel(
    randomUUID().toString(),
    "2022-11-07T08:35:54.140Z",
    NotificationType.RESPOND_ACCEPTED,
    DELETED,
    DemoNotificationParentModel,
    DemoFeedBackModel
)

val DemoNotificationModelList = listOf(
    DemoNotificationMeetingOverModel,
    DemoNotificationRespondAccept,
)