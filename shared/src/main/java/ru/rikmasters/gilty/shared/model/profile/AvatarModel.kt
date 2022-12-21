package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.model.enumeration.PhotoType

sealed interface ImageModel {
    
    val id: String
    val albumId: String
    val ownerId: String
    val type: PhotoType
    val mimeType: String
    val fileSize: Int
    val width: Int
    val height: Int
    val resolutionX: Int
    val resolutionY: Int
    val playtime: Number
    val hasAccess: Boolean
}

data class AvatarModel(
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
): ImageModel

val DemoAvatarModel = AvatarModel(
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
    false
)

val DemoAvatarAccessModel = AvatarModel(
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

