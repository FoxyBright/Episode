package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.model.enumeration.PhotoType
import ru.rikmasters.gilty.shared.model.image.ThumbnailModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import java.util.UUID.randomUUID

data class Avatar(
    val id: String? = null,
    val albumId: String? = null,
    val ownerId: String? = null,
    val type: String? = null,
    val thumbnail: Thumbnail? = null,
    val mimetype: String? = null,
    val filesize: Int? = null,
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val hasAccess: Boolean? = null,
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
        id = id ?: "",
        albumId = albumId ?: "",
        ownerId = ownerId ?: "",
        type = PhotoType.PHOTO,
        thumbnail = thumbnail?.map() ?: ThumbnailModel(),
        mimetype = mimetype ?: "image/jpeg",
        filesize = filesize ?: 0,
        url = url ?: "",
        width = width ?: 0,
        height = height ?: 0,
        hasAccess = false,
    )
}