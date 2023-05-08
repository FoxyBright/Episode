package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.model.meeting.DemoUserModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel

data class MemberMessageModel(
    val author: UserModel,
    val text: String,
    val attachments: List<ChatAttachmentModel>?,
    val is_author: Boolean,
)

val DemoMemberMessageModel = MemberMessageModel(
    DemoUserModel, "anything message",
    listOf(DemoChatAttachmentModel), true
)
