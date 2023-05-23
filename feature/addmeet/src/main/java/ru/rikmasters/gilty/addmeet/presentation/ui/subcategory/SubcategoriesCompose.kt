package ru.rikmasters.gilty.addmeet.presentation.ui.subcategory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.CloseAddMeetAlert
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Dashes
import ru.rikmasters.gilty.bubbles.Bubbles
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.DemoCategoryModel
import ru.rikmasters.gilty.shared.shared.ClosableActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class SubcategoriesState(
    val subcategoryList: List<CategoryModel>,
    val selectCategory: CategoryModel?,
    val online: Boolean,
    val alert: Boolean = false,
)

interface SubcategoriesCallback {

    fun onCategoryClick(category: CategoryModel)
    fun onClose()
    fun onCloseAlert(state: Boolean)
    fun onBack()
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SubcategoriesContent(
    modifier: Modifier = Modifier,
    state: SubcategoriesState,
    callback: SubcategoriesCallback? = null,
) {
    Scaffold(
        modifier,
        topBar = {
            ClosableActionBar(
                stringResource(R.string.add_meet_create_title),
                Modifier,
                stringResource(R.string.add_meet_create_correct_description),
                { callback?.onCloseAlert(true) }, {
                    callback?.onBack()
                }
            )
        },
        bottomBar = {
            Dashes(
                (4), (0), Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
                    .padding(horizontal = 16.dp),
                color = if (state.online)
                    MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.primary
            )
        }
    ) {
        if (LocalInspectionMode.current)
            BubblesForPreview(state, callback)
        else
            Bubbles(
                state.subcategoryList,
                CATEGORY_ELEMENT_SIZE.dp,
                Modifier
                    .padding(it)
                    .padding(top = 8.dp),
            ) { element ->
                CategoryItem(
                    element.name, element.emoji, element.color,
                    (element == state.selectCategory), modifier
                ) { callback?.onCategoryClick(element) }
            }
    }
    CloseAddMeetAlert(
        state.alert, state.online,
        { callback?.onCloseAlert(false) },
        { callback?.onCloseAlert(false); callback?.onClose() })
}

@Composable
private fun BubblesForPreview(
    state: SubcategoriesState,
    callback: SubcategoriesCallback? = null,
) {
    LazyRow(
        Modifier
            .height((CATEGORY_ELEMENT_SIZE * 4).dp)
            .padding(top = 50.dp)
    ) {
        itemsIndexed(state.subcategoryList.chunked(3))
        { index, item ->
            Box(Modifier.fillMaxHeight()) {
                Column(
                    Modifier.align(
                        if (index % 3 != 0) Alignment.TopCenter
                        else Alignment.BottomCenter
                    )
                ) {
                    for (element in item) CategoryItem(
                        element.name, element.emoji, element.color,
                        (element == state.selectCategory)
                    ) { callback?.onCategoryClick(element) }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SubcategoriesPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                MaterialTheme.colorScheme.background
            )
        ) {
            SubcategoriesContent(
                Modifier,
                SubcategoriesState(
                    listOf(DemoCategoryModel),
                    DemoCategoryModel, (false)
                )
            )
        }
    }
}
