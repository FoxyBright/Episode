package ru.rikmasters.gilty.addmeet.presentation.ui.category

import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Dashes
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModelList
import ru.rikmasters.gilty.shared.model.meeting.ShortCategoryModel
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.common.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.shared.CrossButton
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
    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            ActionBar(
                stringResource(R.string.add_meet_create_title),
                stringResource(R.string.add_meet_create_description),
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
        CrossButton(
            Modifier
                .padding(top = 32.dp, end = 16.dp)
                .size(20.dp)
                .align(TopEnd)
        ) { callback?.onBack() }
        Column(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            Center, CenterHorizontally
        ) {
            GradientButton(
                Modifier, stringResource(R.string.next_button)
            ) { callback?.onNext() }
            Dashes((5), (0), Modifier.padding(top = 16.dp))
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