package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

data class NotificationParentModel(
    val meeting: MeetingModel?,
    val file: FileModel?,
    val notification: ShortNotification?,
)

val DemoNotificationParentModel = NotificationParentModel(
    DemoMeetingModel,
    DemoFileModel,
    DemoShortNotification
)