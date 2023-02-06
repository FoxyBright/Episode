package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.enumeration.NotificationStatus
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

data class FullNotify(
    val id: String,
    val date: String,
    val type: NotificationType,
    val status: NotificationStatus,
    val parent: NotificationParentModel,
    val respond: RespondModel,
)

data class ShortNotification(
    val id: String,
    val name: String,
    val description: String,
)

data class NotificationModel(
    val id: Int,
    val meeting: MeetingModel,
    val type: NotificationType,
    val date: String,
)

val DemoNotificationLeaveEmotionModel = NotificationModel(
    1, DemoMeetingModel,
    NotificationType.LEAVE_EMOTIONS,
    "2022-11-07T08:35:54.140Z"
)

val DemoNotificationMeetingOverModel = NotificationModel(
    1, DemoMeetingModel,
    NotificationType.MEETING_OVER,
    "2022-10-17T08:35:54.140Z"
)

val DemoTodayNotificationRespondAccept = NotificationModel(
    1, DemoMeetingModel,
    NotificationType.RESPOND_ACCEPT,
    "2022-10-21T17:40:54.140Z"
)

val DemoTodayNotificationMeetingOver = NotificationModel(
    1, DemoMeetingModel,
    NotificationType.MEETING_OVER,
    "2022-11-09T17:26:00.140Z"
)

val DemoNotificationModelList = listOf(
    DemoNotificationLeaveEmotionModel,
    DemoNotificationMeetingOverModel,
    DemoTodayNotificationRespondAccept,
    DemoTodayNotificationMeetingOver,
)