package ru.rikmasters.gilty.presentation.ui.presentation.login

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
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.CategoryModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoCategoryModelList
import ru.rikmasters.gilty.presentation.ui.shared.ActionBar
import ru.rikmasters.gilty.presentation.ui.shared.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.presentation.ui.shared.CategoryItem
import ru.rikmasters.gilty.presentation.ui.shared.CategoryItemCallback
import ru.rikmasters.gilty.presentation.ui.shared.CategoryItemState
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme

data class SelectCategoriesState(
    val categoryList: List<CategoryModel>,
    val selectCategories: List<CategoryModel>
)

@Composable
@ExperimentalMaterial3Api
fun SelectCategories(
    modifier: Modifier,
    state: SelectCategoriesState,
    callback: CategoryItemCallback? = null
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
                                    CategoryItemState(
                                        state.selectCategories.contains(element)
                                    ),
                                    callback = callback
                                )
                        }
                    }
                }
            }
        }
        GradientButton(
            { callback?.onNext() },
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.next_button)
        )
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(showBackground = true)
private fun SelectCategoriesPreview() {
    GiltyTheme {
        SelectCategories(
            Modifier,
            SelectCategoriesState(DemoCategoryModelList, arrayListOf())
        )
    }
}