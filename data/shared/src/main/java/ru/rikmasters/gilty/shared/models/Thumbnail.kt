package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.image.ThumbnailModel
import java.util.UUID
import java.util.UUID.randomUUID

data class Thumbnail(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    val filesize: Int,
    val mimetype: String,
) {
    
    constructor(): this(
        randomUUID().toString(),
        (""), (0), (0), (0), ("")
    )
    
    fun map() = ThumbnailModel(
        id, url, width, height,
        filesize, mimetype
    )
}