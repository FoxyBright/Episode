package ru.rikmasters.gilty.login.presentation.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.bubbles.Bubbles
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModelList
import ru.rikmasters.gilty.shared.model.meeting.ShortCategoryModel
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.common.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class CategoriesState(
    val categoryList: List<ShortCategoryModel>,
    val selectCategories: List<ShortCategoryModel>
)

interface CategoriesCallback : NavigationInterface {
    fun onCategoryClick(category: ShortCategoryModel) {}
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
                    element,
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
                        if (index % 3 != 0) Alignment.TopCenter
                        else Alignment.BottomCenter
                    )
                ) {
                    for (element in item)
                        CategoryItem(
                            element,
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
            CategoriesState(DemoShortCategoryModelList, arrayListOf())
        )
    }
}