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
)

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