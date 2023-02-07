package ru.rikmasters.gilty.shared.models

import android.graphics.Color.parseColor
import androidx.compose.ui.graphics.Color
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.image.EmojiModel.Companion.categoryIcon
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

data class Category(
    
    val id: String,
    
    val name: String,
    
    val color: String,
    
    val iconType: String,
    
    val children: List<Category>? = null,
): DomainEntity {
    
    fun map() = CategoryModel(
        id = id,
        name = name,
        color = Color(color.getColor()),
        emoji = categoryIcon(iconType),
        children = children.childMap()
    )
    
    private fun List<Category>?.childMap(): List<CategoryModel>? =
        this?.map { it.map().copy(parentId = id) }
    
    private fun String.getColor() =
        parseColor(this)
}