package ru.rikmasters.gilty.mainscreen.presentation.ui.categories

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.DemoFullCategoryModelList

@Composable
fun CategoriesScreen(nav: NavState = get()) {
    val list = remember { mutableStateListOf<Boolean>() }
    repeat(DemoFullCategoryModelList.size) { list.add(false) }
    CategoryList(
        CategoryListState(DemoFullCategoryModelList, list), Modifier.padding(16.dp),
        object : CategoryListCallback {
            override fun onCategoryClick(index: Int, it: Boolean) {
                list[index] = !it
            }
        })
}