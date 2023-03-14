package ru.rikmasters.gilty.shared.model.meeting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import ru.rikmasters.gilty.shared.model.image.DemoCategoryEmoji
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import java.util.UUID.randomUUID

data class CategoryModel(
    
    val id: String,
    
    val name: String,
    
    val color: Color,
    
    val emoji: EmojiModel,
    
    val children: List<CategoryModel>?,
    
    val parentId: String? = null,
) {
    
    constructor(): this(
        id = randomUUID().toString(),
        name = "", color = Black,
        emoji = EmojiModel(type = "", path = ""),
        children = null
    )
}

private val demoChildCategoryModel = CategoryModel(
    "0", "Кино",
    Red, DemoCategoryEmoji, (null)
)

val DemoCategoryModel = CategoryModel(
    "0", "Развлечения",
    Blue, DemoCategoryEmoji,
    listOf(
        demoChildCategoryModel,
        demoChildCategoryModel,
        demoChildCategoryModel
    )
)

val DemoCategoryModelList = listOf(
    DemoCategoryModel,
    DemoCategoryModel,
    DemoCategoryModel,
    DemoCategoryModel
)