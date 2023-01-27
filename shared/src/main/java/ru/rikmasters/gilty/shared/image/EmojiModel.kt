package ru.rikmasters.gilty.shared.image

import ru.rikmasters.gilty.shared.R.drawable.*
import ru.rikmasters.gilty.shared.image.EmojiModel.Companion.categoryIcon
import ru.rikmasters.gilty.shared.image.EmojiModel.Companion.getEmoji

data class EmojiModel(
    
    val type: String,
    
    val path: String,
) {
    
    companion object {
        
        val list = listOf(
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
        
        val badEmoji = list[1]
        fun getEmoji(icon: String) = when(icon) {
            "HEART_EYES" -> list[0]
            "EXPRESSIONLESS" -> list[1]
            "CRYING" -> list[2]
            "HEART_FACE" -> list[3]
            "STAR_EYES" -> list[4]
            "DEVIL" -> list[5]
            "WATER_DROPS" -> list[6]
            "CLOWN" -> list[7]
            "BROKEN_HEART" -> list[8]
            "FIRE" -> list[9]
            "BUTTERFLY" -> list[10]
            "MONEY_FACE" -> list[11]
            "EYE_ROLL" -> list[12]
            else -> EmojiModel("", "")
        }
        
        fun categoryIcon(icon: String): EmojiModel = when(icon) {
            "BASKETBALL" -> EmojiModel("D", "$ic_sport")
            "BRIEFCASE" -> EmojiModel("D", "$ic_business")
            "EIFFEL_TOWER" -> EmojiModel("D", "$ic_travel")
            "BRUSH" -> EmojiModel("D", "$ic_master_classes")
            "POPCORN" -> EmojiModel("D", "$ic_entertainment")
            "ADULT" -> EmojiModel("D", "$ic_18")
            "COCKTAIL" -> EmojiModel("D", "$ic_party")
            "VASE" -> EmojiModel("D", "$ic_art")
            else -> EmojiModel("", "")
        }
    }
}

val DemoCategoryEmoji = categoryIcon("BASKETBALL")
val DemoEmojiModel = getEmoji("BUTTERFLY")