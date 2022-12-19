package ru.rikmasters.gilty.addmeet.presentation.ui.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
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
import ru.rikmasters.gilty.bubbles.Bubbles
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.CATEGORY_ELEMENT_SIZE
import ru.rikmasters.gilty.shared.common.CategoryItem
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType
import ru.rikmasters.gilty.shared.shared.ClosableActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

data class CategoriesState(
    val categoryList: List<CategoriesType>,
    val selectCategories: List<CategoriesType>,
    val alert: Boolean
)

interface CategoriesCallback {
    
    fun onCategoryClick(category: CategoriesType) {}
    fun onClose() {}
    fun onCloseAlert(it: Boolean) {}
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
            ClosableActionBar(
                stringResource(R.string.add_meet_create_title),
                stringResource(R.string.add_meet_create_description),
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
            CategoriesState(
                CategoriesType.values().toList(),
                arrayListOf(), false
            )
        )
    }
}