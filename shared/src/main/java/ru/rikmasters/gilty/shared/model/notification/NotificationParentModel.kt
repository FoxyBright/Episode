package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel

data class NotificationParentModel(
    val meeting: FullMeetingModel,
    val file: FileModel,
    val notification: ShortNotification,
)