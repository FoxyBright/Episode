package ru.rikmasters.gilty.auth.meetings

import android.graphics.Color.parseColor
import androidx.compose.ui.graphics.Color
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.profile.getCategoryIcons

data class Category(
    
    val id: String,
    
    val name: String,
    
    val color: String,
    
    val iconType: String,
    
    val children: List<Category>? = emptyList(),
): DomainEntity {
    
    fun map() = CategoryModel(
        id = id,
        name = name,
        color = Color(color.getColor()),
        emoji = getCategoryIcons(iconType),
        children = children.childMap()
    )
    
    private fun List<Category>?.childMap(): List<CategoryModel> =
        this?.map { it.map() } ?: emptyList()
    
    private fun String.getColor() =
        parseColor(this)
}