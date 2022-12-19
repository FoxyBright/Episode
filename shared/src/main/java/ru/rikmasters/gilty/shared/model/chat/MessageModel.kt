package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModelTwo
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.ImageModel

data class MessageModel(
    val id: String,
    val sender: MemberModel,
    val album: String,
    val text: String,
    val attachments: ImageModel?,
    val hidden: Boolean,
    val isRead: Boolean,
    val isDelivered: Boolean,
    val createdAt: String,
    val answer: MessageModel?
)

val DemoMessageModel = MessageModel(
    id = "1",
    sender = DemoMemberModel,
    album = "Бэтмен",
    text = "Привет",
    attachments = null,
    hidden = false,
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
    attachments = DemoAvatarModel,
    hidden = false,
    isRead = true,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
    answer = DemoMessageModel
)

val DemoHiddenImageMessage = MessageModel(
    id = "1",
    sender = DemoMemberModel,
    album = "Бэтмен",
    text = "",
    attachments = DemoAvatarModel,
    hidden = true,
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
    hidden = false,
    isRead = true,
    isDelivered = true,
    createdAt = "2022-10-17T08:35:54.140Z",
    answer = DemoImageMessage
)
