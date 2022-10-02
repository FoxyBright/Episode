package ru.rikmasters.gilty.presentation.model.profile

import ru.rikmasters.gilty.presentation.model.enumeration.PhotoType

data class AvatarModel(

    val id: String,

    val albumId: String,

    val ownerId: String,

    val type: PhotoType,

    val mimeType: String,

    val fileSize: Int,

    val width: Int,

    val height: Int,

    val resolutionX: Int,

    val resolutionY: Int,

    val playtime: Number

)

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
    0
)
