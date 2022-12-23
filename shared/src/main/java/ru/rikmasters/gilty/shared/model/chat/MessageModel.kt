package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.model.chat.MessageType.MESSAGE
import ru.rikmasters.gilty.shared.model.chat.MessageType.NOTIFICATION
import ru.rikmasters.gilty.shared.model.chat.MessageType.WRITING
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModelTwo
import ru.rikmasters.gilty.shared.model.meeting.MemberModel

enum class MessageType {
    MESSAGE, NOTIFICATION, WRITING
}

data class MessageModel(
    val id: String,
    val sender: MemberModel,
    val album: String,
    val text: String,
    val attachments: AttachmentModel?,
    val notification: ChatNotificationType?,
    val type: MessageType,
    val isRead: Boolean,
    val isDelivered: Boolean,
    val createdAt: String,
    val answer: MessageModel?
)

fun getDemoMessageModel(
    id: String = "1",
    sender: MemberModel = DemoMemberModel,
    album: String = "Бэтмен",
    text: String = "Привет",
    attachments: AttachmentModel? = null,
    notification: ChatNotificationType? = null,
    type: MessageType = MESSAGE,
    isRead: Boolean = false,
    isDelivered: Boolean = true,
    createdAt: String = "2022-10-17T08:35:54.140Z",
    answer: MessageModel? = null
) = MessageModel(
    id, sender, album, text,
    attachments, notification,
    type, isRead, isDelivered,
    createdAt, answer
)

val WritingMessageModel = MessageModel(
    id = "0",
    sender = DemoMemberModelTwo,
    album = "Бэтмен",
    text = "",
    attachments = null,
    notification = null,
    type = WRITING,
    isRead = false,
    isDelivered = false,
    createdAt = "2022-10-17T08:35:54.140Z",
    answer = null
)

val DemoMessageModel = MessageModel(
    id = "1",
    sender = DemoMemberModel,
    album = "Бэтмен",
    text = "Привет",
    attachments = null,
    notification = null,
    type = MESSAGE,
    isRead = false,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
    answer = null
)

val DemoImageMessage = MessageModel(
    id = "1",
    sender = DemoMemberModel,
    album = "Бэтмен",
    text = "",
    attachments = DemoAttachmentModelPhoto,
    notification = null,
    type = MESSAGE,
    isRead = true,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
    answer = DemoMessageModel
)

val DemoHiddenImageMessage = MessageModel(
    id = "1",
    sender = DemoMemberModelTwo,
    album = "Бэтмен",
    text = "",
    attachments = DemoAttachmentModelHiddenAccess,
    notification = null,
    type = MESSAGE,
    isRead = true,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
    answer = DemoMessageModel
)

val DemoMyHiddenImageMessage = MessageModel(
    id = "1",
    sender = DemoMemberModel,
    album = "Бэтмен",
    text = "",
    attachments = DemoAttachmentModelHidden,
    notification = null,
    type = MESSAGE,
    isRead = true,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
    answer = DemoMessageModel
)

val DemoMessageModelLongMessage = MessageModel(
    id = "1",
    sender = DemoMemberModelTwo,
    album = "Бэтмен",
    text = "Как ты? Давно не виделись",
    attachments = null,
    notification = null,
    type = MESSAGE,
    isRead = true,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
    answer = DemoImageMessage
)

val DemoMessageModelVeryLong = getDemoMessageModel(
    text = "МЫ РАСТЕМ КАК КАКТУСЫ,МЫ РАСТЕМ КАК КАКТУСЫ," +
            "МЫ РАСТЕМ КАК КАКТУСЫ,МЫ РАСТЕМ КАК КАКТУСЫ,МЫ РАСТЕМ КАК КАКТУСЫ"
)

val DemoMessageModelCreated = getDemoMessageModel(
    type = NOTIFICATION,
    notification = ChatNotificationType(
        SystemMessageType.CHAT_CREATED, null
    )
)

val DemoMessageModelJoinToChat = getDemoMessageModel(
    type = NOTIFICATION,
    notification = ChatNotificationType(
        SystemMessageType.MEMBER_JOIN,
        DemoMemberModel
    )
)
val DemoMessageModelLeaveChat = getDemoMessageModel(
    type = NOTIFICATION,
    notification = ChatNotificationType(
        SystemMessageType.MEMBER_LEAVE,
        DemoMemberModel
    )
)
val DemoMessageModelScreenshot = getDemoMessageModel(
    type = NOTIFICATION,
    notification = ChatNotificationType(
        SystemMessageType.MEMBER_SCREENSHOT, DemoMemberModel
    )
)
val DemoMessageModel5Minutes = getDemoMessageModel(
    type = NOTIFICATION,
    notification = ChatNotificationType(
        SystemMessageType.TRANSLATION_START_5, null
    )
)
val DemoMessageModel30Minutes = getDemoMessageModel(
    type = NOTIFICATION,
    notification = ChatNotificationType(
        SystemMessageType.TRANSLATION_START_30, null
    )
)


val MessageList = listOf(
    DemoMessageModelCreated,
    DemoMessageModel,
    DemoMessageModelJoinToChat,
    DemoImageMessage,
    DemoMessageModelScreenshot,
    DemoMyHiddenImageMessage,
    DemoMessageModelLeaveChat,
    DemoHiddenImageMessage,
    DemoMessageModel5Minutes,
    DemoMessageModelLongMessage,
    DemoMessageModel30Minutes,
    DemoMessageModelVeryLong
)
