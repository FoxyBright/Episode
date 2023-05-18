package ru.rikmasters.gilty.shared.model.meeting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
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
        name = "", color = Gray,
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
    Color(0xFFFF2E00),
    DemoCategoryEmoji,
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
    DemoCategoryModel,
    DemoCategoryModel,
    DemoCategoryModel,
    DemoCategoryModel,
    DemoCategoryModel,
    DemoCategoryModel,
)