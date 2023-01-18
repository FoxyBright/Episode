package ru.rikmasters.gilty.login.presentation.ui.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import ru.rikmasters.gilty.auth.categories.Category
import ru.rikmasters.gilty.bubbles.Bubbles
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType
import ru.rikmasters.gilty.shared.model.profile.getCategoryIcons
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class CategoriesState(
    val categoryList: List<Category>,
    val selectCategories: List<Category>
)

interface CategoriesCallback {
    
    fun onBack() {}
    fun onNext() {}
    fun onCategoryClick(category: Category) {}
}

@Composable
fun CategoriesContent(
    modifier: Modifier = Modifier,
    state: CategoriesState,
    callback: CategoriesCallback? = null
) {
    Column(
        modifier.fillMaxSize(),
    ) {
        Column(
            Modifier.weight(1f)
        ) {
            ActionBar(
                stringResource(R.string.interested_you),
                stringResource(R.string.interested_you_details),
            ) { callback?.onBack() }
            if(LocalInspectionMode.current)
                BubblesForPreview(state, callback)
            else Bubbles(
                state.categoryList,
                CATEGORY_ELEMENT_SIZE.dp,
                Modifier.padding(top = 8.dp),
            ) { element ->
                CategoryItem(
                    element.name,
                    getCategoryIcons(element.iconType),
                    Color(element.color.toColorInt()),
                    state.selectCategories.contains(element),
                    modifier
                ) { callback?.onCategoryClick(element) }
            }
        }
        GradientButton(
            Modifier
                .wrapContentHeight()
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp),
            stringResource(R.string.next_button)
        ) { callback?.onNext() }
    }
}

@Composable
private fun BubblesForPreview(
    state: CategoriesState,
    callback: CategoriesCallback? = null
) {
    LazyRow(
        Modifier
            .height((CATEGORY_ELEMENT_SIZE * 4).dp)
            .padding(top = 50.dp)
    ) {
        itemsIndexed(state.categoryList.chunked(3))
        { index, item ->
            Box(Modifier.fillMaxHeight()) {
                Column(
                    Modifier.align(
                        if(index % 3 != 0) Alignment.TopCenter
                        else Alignment.BottomCenter
                    )
                ) {
                    for(element in item)
                        CategoryItem(
                            element.name,
                            getCategoryIcons(element.iconType),
                            Color(element.color.toColorInt()),
                            state.selectCategories.contains(element)
                        ) { callback?.onCategoryClick(element) }
                }
            }
        }
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun CategoriesPreview() {
    GiltyTheme {
        CategoriesContent(
            Modifier,
            CategoriesState(CategoriesType.list().map {
                Category(
                    it.name, it.display,
                    it.color.toString(),
                    it.name, listOf()
                )
            }, arrayListOf())
        )
    }
}