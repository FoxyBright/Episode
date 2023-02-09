package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.image.ThumbnailModel
import ru.rikmasters.gilty.shared.model.enumeration.PhotoType
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel

data class FileModel(
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
    val resolutionX: Int,
    val resolutionY: Int,
    val playTime: Int,
) {
    
    fun map() = AvatarModel(
        id, albumId, ownerId,
        type, thumbnail, mimetype,
        filesize, url, width, height,
        hasAccess
    )
}

private val demo = DemoAvatarModel
val DemoFileModel = FileModel(
    demo.id, demo.albumId, demo.ownerId,
    demo.type, demo.thumbnail, demo.mimetype,
    demo.filesize, demo.url, demo.width, demo.height,
    demo.hasAccess, (0), (0), (0)
)