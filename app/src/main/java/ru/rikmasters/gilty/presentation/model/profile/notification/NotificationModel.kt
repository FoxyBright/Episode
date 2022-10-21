package ru.rikmasters.gilty.presentation.model.profile.notification

import ru.rikmasters.gilty.presentation.model.enumeration.NotificationType
import ru.rikmasters.gilty.presentation.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.FullMeetingModel

data class NotificationModel(
    val id: Int,
    val meeting: FullMeetingModel,
    val type: NotificationType,
    val date: String,
)

val DemoNotificationLeaveEmotionModel = NotificationModel(
    1, DemoFullMeetingModel,
    NotificationType.LEAVE_EMOTIONS,
    "2022-10-16T08:35:54.140Z"
)

val DemoNotificationMeetingOverModel = NotificationModel(
    1, DemoFullMeetingModel,
    NotificationType.MEETING_OVER,
    "2022-10-17T08:35:54.140Z"
)

val DemoTodayNotificationRespondAccept = NotificationModel(
    1, DemoFullMeetingModel,
    NotificationType.RESPOND_ACCEPT,
    "2022-10-21T07:35:54.140Z"
)

val DemoTodayNotificationMeetingOver = NotificationModel(
    1, DemoFullMeetingModel,
    NotificationType.MEETING_OVER,
    "2022-10-21T00:00:00.140Z"
)