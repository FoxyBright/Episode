package ru.rikmasters.gilty.chat.presentation.model

import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.ImageModel

data class MessageModel(
    val id: String,
    val sender: MemberModel,
    val album: String,
    val text: String,
    val attachments: ImageModel?,
    val isRead: Boolean,
    val isDelivered: Boolean,
    val createdAt: String
)

val DemoMessageModel = MessageModel(
    id = "1",
    sender = DemoMemberModel,
    album = "Бэтмен",
    text = "Привет",
    attachments = null,
    isRead = false,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
)

val DemoImageMessage = MessageModel(
    id = "1",
    sender = DemoMemberModel,
    album = "Бэтмен",
    text = "",
    attachments = DemoAvatarModel,
    isRead = true,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
)

val DemoMessageModelLongMessage = MessageModel(
    id = "1",
    sender = DemoMemberModel,
    album = "Бэтмен",
    text = "Как ты? Давно не виделись",
    attachments = null,
    isRead = true,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
)
