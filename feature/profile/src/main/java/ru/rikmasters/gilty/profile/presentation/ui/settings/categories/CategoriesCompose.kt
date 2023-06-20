package ru.rikmasters.gilty.profile.presentation.ui.settings.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.bubbles.Bubbles
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.DemoCategoryModel
import ru.rikmasters.gilty.shared.shared.ClosableActionBar
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class CategoriesState(
    val categoryList: List<CategoryModel>,
    val selectCategories: List<CategoryModel>,
    val alert: Boolean,
    val sleep: Boolean,
    val hasParentCategory: Boolean,
)

interface CategoriesCallback {

    fun onCategoryClick(category: CategoryModel) {}
    fun onNext() {}
    fun onClose() {}
    fun onBack() {}
    fun onCloseAlert(it: Boolean) {}
}

@Composable
fun CategoriesContent(
    modifier: Modifier = Modifier,
    state: CategoriesState,
    callback: CategoriesCallback? = null,
) {
    Column(modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            ClosableActionBar(
                title = stringResource(R.string.interested_you),
                details = stringResource(R.string.interested_you_details),
                onClose = { callback?.onCloseAlert(true) },
                onBack = {
                    callback?.onBack()
                }
            )
            if (LocalInspectionMode.current)
                BubblesForPreview(state, callback)
            else if (state.sleep) Bubbles(
                data = state.categoryList,
                elementSize = CATEGORY_ELEMENT_SIZE.dp,
                modifier = Modifier.padding(
                    top = 32.dp,
                    bottom = 10.dp
                ),
            ) { element ->
                CategoryItem(
                    name = element.name,
                    icon = element.emoji,
                    color = element.color,
                    state = state.selectCategories.contains(element),
                ) { callback?.onCategoryClick(element) }
            }
        }
        GradientButton(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp),
            if (state.hasParentCategory) stringResource(R.string.next_button)
            else stringResource(R.string.save_button)
        ) { callback?.onNext() }
    }
    val dismiss = { callback?.onCloseAlert(false) }
    GAlert(
        show = state.alert,
        success = Pair(stringResource(R.string.exit_button))
        { dismiss(); callback?.onClose() },
        label = stringResource(R.string.add_meet_exit_alert_details),
        title = stringResource(R.string.change_user_categories_alert),
        onDismissRequest = { dismiss() },
        cancel = Pair(stringResource(R.string.cancel_button)) { dismiss() }
    )
}

@Composable
private fun BubblesForPreview(
    state: CategoriesState,
    callback: CategoriesCallback? = null,
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
                        if (index % 3 != 0) TopCenter
                        else BottomCenter
                    )
                ) {
                    for (element in item)
                        CategoryItem(
                            name = element.name,
                            icon = element.emoji,
                            color = element.color,
                            state = state.selectCategories.contains(element)
                        ) { callback?.onCategoryClick(element) }
                }
            }
        }
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(
    backgroundColor = 0xFFE8E8E8,
    showBackground = true
)
private fun CategoriesPreview() {
    GiltyTheme {
        CategoriesContent(
            state = CategoriesState(
                listOf(DemoCategoryModel),
                emptyList(), (false), true, false
            )
        )
    }
}