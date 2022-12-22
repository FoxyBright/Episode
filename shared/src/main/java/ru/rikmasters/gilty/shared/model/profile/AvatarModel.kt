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

fun getDemoAvatarModel(
    id: String = "https://placekitten.com/1200/800",
    albumId: String = "test",
    ownerId: String = "test",
    type: PhotoType = PhotoType.PHOTO,
    mimeType: String = "mimeType",
    fileSize: Int = 10,
    width: Int = 400,
    height: Int = 800,
    resolutionX: Int = 400,
    resolutionY: Int = 800,
    playtime: Number = 0,
    hasAccess: Boolean = false
) = AvatarModel(
    id, albumId, ownerId,
    type, mimeType, fileSize,
    width, height, resolutionX,
    resolutionY, playtime, hasAccess
)

val DemoAvatarModel =
    getDemoAvatarModel()

val DemoAvatarAccessModel =
    getDemoAvatarModel(hasAccess = true)

