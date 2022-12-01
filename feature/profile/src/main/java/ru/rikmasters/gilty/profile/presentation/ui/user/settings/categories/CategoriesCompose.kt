package ru.rikmasters.gilty.profile.presentation.ui.user.settings.categories

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            ClosableActionBar(
                stringResource(R.string.add_meet_create_title),
                stringResource(R.string.add_meet_create_description),
                Modifier, { callback?.onCloseAlert(true) }
            )
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
            stringResource(R.string.save_button)
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