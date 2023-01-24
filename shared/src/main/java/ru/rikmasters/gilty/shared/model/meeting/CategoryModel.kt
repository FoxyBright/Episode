package ru.rikmasters.gilty.shared.model.meeting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import ru.rikmasters.gilty.shared.model.profile.DemoCategoryEmoji
import ru.rikmasters.gilty.shared.model.profile.EmojiModel

data class CategoryModel(
    
    val id: String,
    
    val name: String,
    
    val color: Color,
    
    val emoji: EmojiModel,
    
    val children: List<CategoryModel>?,
)

private val DemoChildCategoryModel = CategoryModel(
    "0", "Кино",
    Red, DemoCategoryEmoji, (null)
)

val DemoCategoryModel = CategoryModel(
    "0", "Развлечения",
    Green, DemoCategoryEmoji,
    listOf(
        DemoChildCategoryModel,
        DemoChildCategoryModel,
        DemoChildCategoryModel
    )
)