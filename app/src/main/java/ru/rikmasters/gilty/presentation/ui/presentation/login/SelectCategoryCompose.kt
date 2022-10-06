package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.CategoryModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoCategoryModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoCategoryModelList
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.shared.ActionBar
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

data class SelectCategoriesState(
    val categoryList: List<CategoryModel>,
    val selectCategories: List<CategoryModel>
)

interface SelectCategoriesCallback : NavigationInterface {
    fun onCategoryClick(category: CategoryModel)
}

private const val ELEMENT_SIZE = 135

@Composable
@ExperimentalMaterial3Api
fun SelectCategories(
    modifier: Modifier,
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
                    .height((ELEMENT_SIZE * 4).dp)
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
                                    state.selectCategories.contains(element),
                                    callback
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
fun CategoryItem(
    item: CategoryModel,
    state: Boolean = false,
    callback: SelectCategoriesCallback? = null
) {
    var iconState by remember { mutableStateOf(state) }
    Box(
        Modifier
            .size((ELEMENT_SIZE + 10).dp)
    ) {
        Box(
            Modifier
                .size(ELEMENT_SIZE.dp)
                .clip(CircleShape)
                .background(
                    if (iconState) Color(item.color.toColorInt())
                    else ThemeExtra.colors.grayIcon
                )
                .align(Alignment.BottomCenter)
                .clickable {
                    iconState = !iconState
                    callback?.onCategoryClick(item)
                },
            Alignment.Center
        ) {
            Text(
                item.name,
                Modifier,
                ThemeExtra.colors.white,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = ThemeExtra.typography.MediumText
            )
        }
        Box(
            Modifier
                .size((ELEMENT_SIZE / 3).dp)
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.BottomStart),
            Alignment.Center
        ) {
            AsyncImage(
                item.emoji.path,
                stringResource(R.string.next_button),
                Modifier.size((ELEMENT_SIZE / 6).dp),
                painterResource(R.drawable.cinema)
            )
        }
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(showBackground = true)
private fun CategoryItemPreview() {
    GiltyTheme {
        CategoryItem(DemoCategoryModel)
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