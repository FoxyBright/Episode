package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.R.drawable.*

data class EmojiModel(
    
    val type: String,
    
    val path: String

)

@Suppress("unused")
val DemoEmojiModelURL = EmojiModel("URL", "https://placekitten.com/1200/800")

fun getCategoryIcons(icon: String): EmojiModel = when(icon) {
    "BASKETBALL" -> EmojiModel("D", "$ic_sport")
    "BRIEFCASE" -> EmojiModel("D", "$ic_business")
    "EIFFEL_TOWER" -> EmojiModel("D", "$ic_travel")
    "BRUSH" -> EmojiModel("D", "$ic_master_classes")
    "POPCORN" -> EmojiModel("D", "$ic_entertainment")
    "ADULT" -> EmojiModel("D", "$ic_18")
    "COCKTAIL" -> EmojiModel("D", "$ic_party")
    "VASE" -> EmojiModel("D", "$ic_art")
    else -> EmojiModel("D", "$ic_image_empty")
}

val EmojiList = listOf(
    EmojiModel("D", "$ic_love"),
    EmojiModel("D", "$ic_bad"),
    EmojiModel("D", "$ic_cry"),
    EmojiModel("D", "$ic_cuty"),
    EmojiModel("D", "$ic_admiration"),
    EmojiModel("D", "$ic_devil"),
    EmojiModel("D", "$ic_drops"),
    EmojiModel("D", "$ic_clown"),
    EmojiModel("D", "$ic_brocken_heart"),
    EmojiModel("D", "$ic_fire"),
    EmojiModel("D", "$ic_batterfly"),
    EmojiModel("D", "$ic_money_love"),
    EmojiModel("D", "$ic_sarcasm"),
)

val badEmoji = EmojiModel(
    "D", "$ic_bad"
)

val DemoEmojiModel = EmojiModel(
    "D", "$ic_batterfly"
)