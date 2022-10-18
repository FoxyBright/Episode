package ru.rikmasters.gilty.presentation.model.profile

data class RatingModel(

    val average: String,

    val frequent: EmojiModel

)

val DemoRatingModel = RatingModel(
    "4.9",
    DemoEmojiModel
)
