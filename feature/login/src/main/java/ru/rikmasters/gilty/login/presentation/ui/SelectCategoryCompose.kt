package ru.rikmasters.gilty.login.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModelList
import ru.rikmasters.gilty.shared.model.meeting.ShortCategoryModel
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.shared.shared.CategoryItem
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class SelectCategoriesState(
    val categoryList: List<ShortCategoryModel>,
    val selectCategories: List<ShortCategoryModel>
)

interface SelectCategoriesCallback : NavigationInterface {
    fun onCategoryClick(category: ShortCategoryModel) {}
}

@Composable
fun SelectCategories(
    modifier: Modifier = Modifier,
    state: SelectCategoriesState,
    callback: SelectCategoriesCallback? = null
) {
    Box(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            ActionBar(
                stringResource(R.string.interested_you),
                stringResource(R.string.interested_you_details),
                Modifier.padding(horizontal = 16.dp)
            ) { callback?.onBack() }
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
        GradientButton(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.next_button)
        ) { callback?.onNext() }
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun SelectCategoriesPreview() {
    GiltyTheme {
        SelectCategories(
            Modifier,
            SelectCategoriesState(DemoShortCategoryModelList, arrayListOf())
        )
    }
}