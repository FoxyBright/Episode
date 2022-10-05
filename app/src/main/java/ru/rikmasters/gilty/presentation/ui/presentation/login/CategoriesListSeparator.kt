package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.runtime.Composable
import ru.rikmasters.gilty.presentation.model.meeting.CategoryModel

@Composable
fun CategoriesListSeparator(categories: List<CategoryModel>): List<List<CategoryModel>> {
    val first = arrayListOf<CategoryModel>()
    val second = arrayListOf<CategoryModel>()
    val third = arrayListOf<CategoryModel>()
    for (category in categories) when {
        categories.indexOf(category) % 3 == 0 -> first.add(category)
        categories.indexOf(category) % 2 == 0 -> second.add(category)
        else -> third.add(category)
    }
    return listOf(first, second, third)
}