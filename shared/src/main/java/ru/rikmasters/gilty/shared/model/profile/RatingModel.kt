package ru.rikmasters.gilty.shared.model.profile

data class RatingModel(

    val average: String,

    val emoji: EmojiModel

)

val DemoRatingModel = RatingModel(
    "4.9",
    DemoEmojiModel
)
