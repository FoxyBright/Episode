package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.image.DemoEmojiModel
import ru.rikmasters.gilty.shared.image.EmojiModel

data class RatingModel(
    val average: String,
    val emoji: EmojiModel,
)

val DemoRatingModel = RatingModel(
    "4.9",
    DemoEmojiModel
)

val DemoRatingModelList = listOf(
    DemoRatingModel,
    DemoRatingModel,
    DemoRatingModel,
    DemoRatingModel,
    DemoRatingModel,
    DemoRatingModel,
    DemoRatingModel,
    DemoRatingModel,
    DemoRatingModel,
    DemoRatingModel,
)
