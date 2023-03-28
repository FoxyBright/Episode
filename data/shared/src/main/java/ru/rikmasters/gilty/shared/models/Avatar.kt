package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.model.enumeration.PhotoType
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import java.util.UUID
import java.util.UUID.randomUUID

data class Avatar(
    val id: String,
    val albumId: String,
    val ownerId: String,
    val type: String,
    val thumbnail: Thumbnail,
    val mimetype: String,
    val filesize: Int,
    val url: String,
    val width: Int,
    val height: Int,
    val hasAccess: Boolean,
    val blockedAt: String? = null,
): DomainEntity {
    
    constructor(): this(
        randomUUID().toString(),
        (""), (""), (""),
        Thumbnail(), (""),
        (0), (""), (0),
        (0), (false)
    )
    
    fun map() = AvatarModel(
        id, albumId, ownerId,
        PhotoType.PHOTO, thumbnail.map(),
        mimetype, filesize, url,
        width, height, hasAccess,
        blockedAt
    )
}