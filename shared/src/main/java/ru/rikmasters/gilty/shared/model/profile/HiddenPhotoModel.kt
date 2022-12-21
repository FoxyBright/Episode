package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.model.enumeration.PhotoType

data class HiddenPhotoModel(
    override val id: String,
    override val albumId: String,
    override val ownerId: String,
    override val type: PhotoType,
    override val mimeType: String,
    override val fileSize: Int,
    override val width: Int,
    override val height: Int,
    override val resolutionX: Int,
    override val resolutionY: Int,
    override val playtime: Number,
    override val hasAccess: Boolean
) : ImageModel

val DemoHiddenPhotoModel = HiddenPhotoModel(
    "https://placekitten.com/1200/800",
    "test",
    "test",
    PhotoType.PHOTO,
    "mimeType",
    10,
    400,
    800,
    400,
    800,
    0,
    true
)