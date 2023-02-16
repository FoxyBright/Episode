package ru.rikmasters.gilty.shared.model.image

import ru.rikmasters.gilty.shared.R.drawable.*
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.categoryIcon
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.getEmoji

data class EmojiModel(
    
    val type: String,
    
    val path: String,
) {
    
    companion object {
        
        val list = listOf(
            EmojiModel("HEART_EYES", "$ic_love"),
            EmojiModel("EXPRESSIONLESS", "$ic_bad"),
            EmojiModel("CRYING", "$ic_cry"),
            EmojiModel("HEART_FACE", "$ic_cuty"),
            EmojiModel("STAR_EYES", "$ic_admiration"),
            EmojiModel("DEVIL", "$ic_devil"),
            EmojiModel("WATER_DROPS", "$ic_drops"),
            EmojiModel("CLOWN", "$ic_clown"),
            EmojiModel("BROKEN_HEART", "$ic_brocken_heart"),
            EmojiModel("FIRE", "$ic_fire"),
            EmojiModel("BUTTERFLY", "$ic_batterfly"),
            EmojiModel("MONEY_FACE", "$ic_money_love"),
            EmojiModel("EYE_ROLL", "$ic_sarcasm"),
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
            else -> EmojiModel("URL", "")
        }
        
        fun categoryIcon(icon: String): EmojiModel = when(icon) {
            "BASKETBALL" -> EmojiModel("BASKETBALL", "$ic_sport")
            "BRIEFCASE" -> EmojiModel("BRIEFCASE", "$ic_business")
            "EIFFEL_TOWER" -> EmojiModel("EIFFEL_TOWER", "$ic_travel")
            "BRUSH" -> EmojiModel("BRUSH", "$ic_master_classes")
            "POPCORN" -> EmojiModel("POPCORN", "$ic_entertainment")
            "ADULT" -> EmojiModel("ADULT", "$ic_18")
            "COCKTAIL" -> EmojiModel("COCKTAIL", "$ic_party")
            "VASE" -> EmojiModel("VASE", "$ic_art")
            else -> EmojiModel("URL", "")
        }
    }
}

val DemoCategoryEmoji = categoryIcon("BASKETBALL")
val DemoEmojiModel = getEmoji("BUTTERFLY")