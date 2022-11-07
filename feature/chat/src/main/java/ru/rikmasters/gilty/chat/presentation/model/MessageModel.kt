package ru.rikmasters.gilty.chat.presentation.model

import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.ImageModel

sealed interface ChatModel {
    val id: String
    val sender: MemberModel
    val albumId: String
    val text: String
    val attachments: ImageModel?
    val isRead: Boolean
    val isDelivered: Boolean
    val createdAt: String
}

data class MessageModel(
    override val id: String,
    override val sender: MemberModel,
    override val albumId: String,
    override val text: String,
    override val attachments: ImageModel?,
    override val isRead: Boolean,
    override val isDelivered: Boolean,
    override val createdAt: String
) : ChatModel

val DemoMessageModel = MessageModel(
    id = "1",
    sender = DemoMemberModel,
    albumId = "Group",
    text = "Привет",
    attachments = null,
    isRead = false,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
)

val DemoMessageModelWithAnswer = MessageModel(
    id = "1",
    sender = DemoMemberModel,
    albumId = "Group",
    text = "Как ты?",
    attachments = DemoAvatarModel,
    isRead = true,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
)
