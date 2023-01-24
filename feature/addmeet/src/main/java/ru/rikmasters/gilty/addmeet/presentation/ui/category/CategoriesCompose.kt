package ru.rikmasters.gilty.addmeet.presentation.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.MEETING
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.CloseAddMeetAlert
import ru.rikmasters.gilty.addmeet.presentation.ui.extentions.Dashes
import ru.rikmasters.gilty.auth.Auth.logD
import ru.rikmasters.gilty.bubbles.Bubbles
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.DemoCategoryModel
import ru.rikmasters.gilty.shared.shared.ClosableActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class CategoriesState(
    val categoryList: List<CategoryModel>,
    val selectCategory: CategoryModel?,
    val alert: Boolean = false,
)

interface CategoriesCallback {
    
    fun onCategoryClick(category: CategoryModel)
    fun onClose()
    fun onCloseAlert(state: Boolean)
}

@Composable
fun CategoriesContent(
    modifier: Modifier = Modifier,
    state: CategoriesState,
    callback: CategoriesCallback? = null,
) {
    Column(
        modifier.fillMaxSize(),
    ) {
        Column(
            Modifier.weight(1f)
        ) {
            ClosableActionBar(
                stringResource(R.string.add_meet_create_title),
                stringResource(R.string.add_meet_create_description),
                Modifier, { callback?.onCloseAlert(true) }
            )
            logD("list ${state.categoryList}")
            if(LocalInspectionMode.current)
                BubblesForPreview(state, callback)
            else Bubbles(
                state.categoryList,
                CATEGORY_ELEMENT_SIZE.dp,
                Modifier.padding(top = 8.dp),
            ) { element ->
                CategoryItem(
                    element.name, element.emoji, element.color,
                    (element == state.selectCategory), modifier
                ) { callback?.onCategoryClick(DemoCategoryModel) }
            }
        }
        Dashes(
            (4), (0), Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp),
            color = if(MEETING.isOnline)
                colorScheme.secondary
            else colorScheme.primary
        )
    }
    CloseAddMeetAlert(
        state.alert,
        { callback?.onCloseAlert(false) },
        { callback?.onCloseAlert(false); callback?.onClose() })
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
                        if(index % 3 != 0) Alignment.TopCenter
                        else Alignment.BottomCenter
                    )
                ) {
                    for(element in item) CategoryItem(
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
private fun CategoriesPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            CategoriesContent(
                Modifier,
                CategoriesState(
                    listOf(DemoCategoryModel),
                    DemoCategoryModel
                )
            )
        }
    }
}