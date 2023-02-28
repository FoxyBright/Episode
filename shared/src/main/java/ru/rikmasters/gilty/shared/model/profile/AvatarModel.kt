package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.model.enumeration.PhotoType
import ru.rikmasters.gilty.shared.model.enumeration.PhotoType.PHOTO
import ru.rikmasters.gilty.shared.model.image.DemoThumbnailModel
import ru.rikmasters.gilty.shared.model.image.ThumbnailModel

data class AvatarModel(
    val id: String,
    val albumId: String,
    val ownerId: String,
    val type: PhotoType,
    val thumbnail: ThumbnailModel,
    val mimetype: String,
    val filesize: Int,
    val url: String,
    val width: Int,
    val height: Int,
    val hasAccess: Boolean,
    val blockedAt: String? = null,
)

val DemoAvatarModel = AvatarModel(
    id = "https://placekitten.com/1200/800",
    albumId = "test",
    ownerId = "test",
    type = PHOTO,
    thumbnail = DemoThumbnailModel,
    mimetype = "image/jpeg",
    filesize = 10,
    url = "https://placekitten.com/1200/800",
    width = 400,
    height = 800,
    hasAccess = false,
)

val DemoAvatarAccessModel =
    DemoAvatarModel.copy(hasAccess = true)

