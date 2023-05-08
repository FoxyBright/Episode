package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.model.chat.AttachmentType.PHOTO
import ru.rikmasters.gilty.shared.model.chat.AttachmentType.PRIVATE_PHOTO
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarAccessModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel

enum class AttachmentType(val value: String) {
    PHOTO("Фото"),
    PRIVATE_PHOTO("Скрытое фото"),
    VIDEO("Видео")
}

data class AttachmentModel(
    val type: AttachmentType,
    val file: AvatarModel?,
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