package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.model.image.DemoEmojiModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.getEmoji

data class RatingModel(
    val average: String,
    val emoji: EmojiModel,
) {
    constructor(): this(
        ("0.0"), getEmoji("")
    )
}

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
