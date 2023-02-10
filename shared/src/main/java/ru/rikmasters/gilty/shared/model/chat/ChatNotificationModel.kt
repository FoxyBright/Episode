package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.model.meeting.UserModel

enum class SystemMessageType {
    CHAT_CREATED, MEMBER_JOIN, MEMBER_LEAVE,
    MEMBER_SCREENSHOT, TRANSLATION_START_30,
    TRANSLATION_START_5
}

data class ChatNotificationType(
    val type: SystemMessageType,
    val member: UserModel?
)