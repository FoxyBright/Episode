package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.meeting.*

data class NotificationParentModel(
    val meeting: MeetingModel?,
    val file: FileModel?,
    val notification: ShortNotification?,
    val user: UserModel?,
)

val DemoNotificationParentModel = NotificationParentModel(
    DemoMeetingModel,
    DemoFileModel,
    DemoShortNotification,
    DemoUserModel
)