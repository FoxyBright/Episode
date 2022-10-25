package ru.rikmasters.gilty.shared.model.profile

val DemoProfileModel = ProfileModel(
    "0",
    "+7 910 524-12-12",
    "alina.loon",
    DemoEmojiModel,
    DemoGenderModel,
    DemoOrientationModel,
    27,
    "Instagram @cristi",
    DemoAvatarModel,
    DemoRatingModel,
    true
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


