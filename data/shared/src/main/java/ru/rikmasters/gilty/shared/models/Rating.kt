package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.model.profile.RatingModel

data class Rating(
    val emojiType: String,
    val value: Double,
) {
    
    fun map() = RatingModel(
        value.toString(),
        getEmoji(emojiType)
    )
}