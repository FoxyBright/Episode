package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.model.enumeration.ChatNotificationType
import ru.rikmasters.gilty.shared.model.meeting.DemoUserModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel

data class ChatNotificationModel(
    val type: ChatNotificationType,
    val member: UserModel?,
)

val DemoChatNotificationModel = ChatNotificationModel(
    ChatNotificationType.MEMBER_SCREENSHOT,
    DemoUserModel
)