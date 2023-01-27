package ru.rikmasters.gilty.shared.model.meeting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import ru.rikmasters.gilty.shared.image.DemoCategoryEmoji
import ru.rikmasters.gilty.shared.image.EmojiModel

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
    Blue, DemoCategoryEmoji,
    listOf(
        DemoChildCategoryModel,
        DemoChildCategoryModel,
        DemoChildCategoryModel
    )
)