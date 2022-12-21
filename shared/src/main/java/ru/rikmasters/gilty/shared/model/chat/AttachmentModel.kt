package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.model.chat.AttachmentType.PHOTO
import ru.rikmasters.gilty.shared.model.chat.AttachmentType.PRIVATE_PHOTO
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarAccessModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.ImageModel

enum class AttachmentType {
    PHOTO, PRIVATE_PHOTO, VIDEO
}

data class AttachmentModel(
    val type: AttachmentType,
    val file: ImageModel?
)

val DemoAttachmentModelPhoto = AttachmentModel(
    PHOTO, DemoAvatarModel
)

val DemoAttachmentModelHidden = AttachmentModel(
    PRIVATE_PHOTO, DemoAvatarModel
)

val DemoAttachmentModelHiddenAccess = AttachmentModel(
    PRIVATE_PHOTO, DemoAvatarAccessModel
)