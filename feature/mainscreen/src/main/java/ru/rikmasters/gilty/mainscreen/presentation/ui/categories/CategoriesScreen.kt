package ru.rikmasters.gilty.mainscreen.presentation.ui.categories

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.DemoFullCategoryModelList

@Composable
fun CategoriesScreen(
    categoryFilters: (
        List<CategoryModel>,
        List<Pair<CategoryModel, String>>
    ) -> Unit
) {
    val allCategories = DemoFullCategoryModelList
    val stateList =
        remember { mutableStateListOf<Boolean>() }
    val categories =
        remember { mutableStateListOf<CategoryModel>() }
    val subCategories =
        remember { mutableStateListOf<Pair<CategoryModel, String>>() }
    repeat(allCategories.size) { stateList.add(false) }
    CategoryList(
        CategoryListState(
            allCategories, stateList, subCategories
        ), Modifier.padding(16.dp),
        object : CategoryListCallback {
            override fun onCategoryClick(index: Int, it: Boolean) {
                stateList[index] = !it
            }

            override fun onSubSelect(category: CategoryModel, sub: String?) {
                if (sub.isNullOrBlank()) categories.add(category)
                else subCategories.add(Pair(category, sub))
            }

            override fun onDone() {
                categoryFilters(categories, subCategories)
            }

            override fun onClear() {
                repeat(allCategories.size) { stateList[it] = false }
                subCategories.clear(); categories.clear()
            }
        })
}