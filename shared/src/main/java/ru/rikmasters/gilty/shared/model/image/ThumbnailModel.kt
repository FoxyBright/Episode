package ru.rikmasters.gilty.shared.model.image

import ru.rikmasters.gilty.shared.model.enumeration.PhotoType.PHOTO
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import java.util.UUID.randomUUID

data class ThumbnailModel(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    val filesize: Int,
    val mimetype: String,
) {
    
    constructor() : this(
        id = randomUUID().toString(),
        url = "",
        width = 0,
        height = 0,
        filesize = 0,
        mimetype = "image/jpeg",
    )
    
    fun map() = AvatarModel(
        id = id,
        albumId = id,
        ownerId = id,
        type = PHOTO,
        thumbnail = this,
        mimetype = mimetype,
        filesize = filesize,
        url = url,
        width = width,
        height = height,
        hasAccess = true
    )
}

val DemoThumbnailModel = ThumbnailModel(
    id = randomUUID().toString(),
    url = "https://placekitten.com/1200/800",
    width = 400,
    height = 800,
    filesize = 10,
    mimetype = "image/jpeg",
)