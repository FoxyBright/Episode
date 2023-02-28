package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.image.ThumbnailModel

data class Thumbnail(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    val filesize: Int,
    val mimetype: String,
) {
    
    fun map() = ThumbnailModel(
        id, url, width, height,
        filesize, mimetype
    )
}