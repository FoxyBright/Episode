package ru.rikmasters.gilty.presentation.model.profile


data class CreateProfileModel(

    val username: String,

    val genderId: String,

    val age: Int,

    val orientationId: String?,

    val aboutMe: String?

)

val DemoCreateAccountModel = CreateProfileModel(
    "angelika.aeom",
    "2",
    18,
    null,
    null
)

data class ProfileModel(

    val id: String,

    val phone: String,

    val username: String,

    val emoji: EmojiModel,

    val gender: GenderModel,

    val orientation: OrientationModel,

    val age: Int,

    val aboutMe: String,

    val avatar: AvatarModel,

    val rating: RatingModel,

    val isComplete: Boolean
)


