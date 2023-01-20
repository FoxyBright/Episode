package ru.rikmasters.gilty.shared.model.enumeration

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rikmasters.gilty.shared.R.string.*
import ru.rikmasters.gilty.shared.R.drawable.*
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType.*
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

enum class CategoriesType {
    
    SPORT, BUSINESS, TRAVEL, MASTER_CLASSES,
    ENTERTAINMENT, EROTIC, PARTY, ART;
    
    companion object {
        
        private val enums = CategoriesType.values()
        
        fun list() = enums.toList()
        
        fun get(index: Int) = enums[index]
    }
    
    val display
        @Composable get() = stringResource(
            when(this) {
                SPORT -> categories_sport
                BUSINESS -> categories_business
                TRAVEL -> categories_travel
                MASTER_CLASSES -> categories_master_classes
                ENTERTAINMENT -> categories_entertainment
                EROTIC -> categories_erotic
                PARTY -> categories_party
                ART -> categories_art
                
            }
        )
    
    val color
        @Composable get() = when(this) {
            SPORT -> colors.sport
            BUSINESS -> colors.business
            TRAVEL -> colors.travel
            MASTER_CLASSES -> colors.masterClasses
            ENTERTAINMENT -> colors.entertainment
            EROTIC -> colors.erotic
            PARTY -> colors.party
            ART -> colors.art
        }
    
    val emoji
        get() = when(this) {
            SPORT -> EmojiModel("D", "$ic_sport")
            BUSINESS -> EmojiModel("D", "$ic_business")
            TRAVEL -> EmojiModel("D", "$ic_travel")
            MASTER_CLASSES -> EmojiModel("D", "$ic_master_classes")
            ENTERTAINMENT -> EmojiModel("D", "$ic_entertainment")
            EROTIC -> EmojiModel("D", "$ic_18")
            PARTY -> EmojiModel("D", "$ic_party")
            ART -> EmojiModel("D", "$ic_art")
        }
    
    val subs
        @Composable get() = when(this) {
            SPORT -> null
            BUSINESS -> null
            TRAVEL -> null
            MASTER_CLASSES -> null
            ENTERTAINMENT -> listOf(
                stringResource(sub_categories_restaurant),
                stringResource(sub_categories_cinema),
                stringResource(sub_categories_cafe),
                stringResource(sub_categories_walking)
            )
            
            EROTIC -> null
            PARTY -> null
            ART -> null
        }
}

fun getCategoriesType(name: String) = when(name) {
    "BASKETBALL" -> SPORT
    "BRIEFCASE" -> BUSINESS
    "EIFFEL_TOWER" -> TRAVEL
    "BRUSH" -> MASTER_CLASSES
    "POPCORN" -> ENTERTAINMENT
    "ADULT" -> EROTIC
    "COCKTAIL" -> PARTY
    else -> ART
}