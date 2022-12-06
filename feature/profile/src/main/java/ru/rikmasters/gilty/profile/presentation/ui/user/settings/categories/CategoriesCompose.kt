package ru.rikmasters.gilty.profile.presentation.ui.user.settings.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.bubbles.Bubbles
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModelList
import ru.rikmasters.gilty.shared.model.meeting.ShortCategoryModel
import ru.rikmasters.gilty.shared.shared.ClosableActionBar
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class CategoriesState(
    val categoryList: List<ShortCategoryModel>,
    val selectCategories: List<ShortCategoryModel>,
    val alert: Boolean
)

interface CategoriesCallback {
    
    fun onCategoryClick(category: ShortCategoryModel) {}
    fun onNext() {}
    fun onClose() {}
    fun onCloseAlert(it: Boolean) {}
}

@Composable
fun CategoriesContent(
    modifier: Modifier = Modifier,
    state: CategoriesState,
    callback: CategoriesCallback? = null
) {
    Column(modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            ClosableActionBar(
                stringResource(R.string.interested_you),
                stringResource(R.string.interested_you_details),
                Modifier, { callback?.onCloseAlert(true) }
            )
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
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp),
            stringResource(R.string.next_button)
        ) { callback?.onNext() }
    }
    GAlert(state.alert, { callback?.onCloseAlert(false) },
        stringResource(R.string.change_user_categories_alert),
        Modifier, stringResource(R.string.add_meet_exit_alert_details),
        success = Pair(stringResource(R.string.exit_button))
        { callback?.onCloseAlert(false); callback?.onClose() },
        cancel = Pair(stringResource(R.string.cancel_button))
        { callback?.onCloseAlert(false) })
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
            CategoriesState(DemoShortCategoryModelList, arrayListOf(), false)
        )
    }
}