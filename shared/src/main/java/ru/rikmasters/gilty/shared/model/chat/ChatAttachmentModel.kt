package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel

data class ChatAttachmentModel(
    val type: AttachmentType,
    val file: AvatarModel? = null,
)

val DemoChatAttachmentModel = ChatAttachmentModel(
    AttachmentType.PHOTO,
    DemoAvatarModel
)
