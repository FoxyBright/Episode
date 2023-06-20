package ru.rikmasters.gilty.addmeet.presentation.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

data class CategoriesState(
    val categoryList: List<CategoryModel>,
    val selectCategory: CategoryModel?,
    val online: Boolean,
    val alert: Boolean = false,
)

interface CategoriesCallback {

    fun onCategoryClick(category: CategoryModel)
    fun onClose()
    fun onCloseAlert(state: Boolean)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CategoriesContent(
    modifier: Modifier = Modifier,
    state: CategoriesState,
    callback: CategoriesCallback? = null,
) {
    var isAnimating by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            ClosableActionBar(
                title = stringResource(R.string.add_meet_create_title),
                details = stringResource(R.string.add_meet_create_description),
                onClose = { callback?.onCloseAlert(true) }
            )
        },
        bottomBar = {
            Dashes(
                count = 4,
                active = 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
                    .padding(horizontal = 16.dp),
                color = if (state.online)
                    colorScheme.secondary
                else colorScheme.primary
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            if (LocalInspectionMode.current)
                BubblesForPreview(state, callback)
            else Bubbles(
                data = state.categoryList,
                elementSize = CATEGORY_ELEMENT_SIZE.dp,
                modifier = Modifier
                    .padding(
                        top = 66.dp,
                        bottom = 10.dp
                    ),
            ) { element ->
                CategoryItem(
                    modifier = Modifier,
                    name = element.name,
                    icon = element.emoji,
                    color = element.color,
                    state = element == state.selectCategory,
                    isAnimating = isAnimating,
                ) {
                    scope.launch {
                        isAnimating = true
                        callback?.onCategoryClick(element)
                        delay(600L)
                        isAnimating = false
                    }
                }
            }
        }
    }
    CloseAddMeetAlert(
        state.alert, state.online,
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
                    DemoCategoryModel, (false)
                )
            )
        }
    }
}