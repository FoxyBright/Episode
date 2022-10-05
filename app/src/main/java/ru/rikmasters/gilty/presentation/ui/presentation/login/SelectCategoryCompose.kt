package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import ru.rikmasters.gilty.presentation.model.meeting.DemoCategoryModelList
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.shared.LoginActionBar
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

data class SelectCategoriesState(
    val categoryList: List<CategoryModel>
)

@Composable
@ExperimentalMaterial3Api
fun SelectCategories(
    modifier: Modifier,
    state: SelectCategoriesState,
    callback: NavigationInterface? = null
) {
    val list = listSeparator(state.categoryList)
    Box(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LoginActionBar(
                stringResource(R.string.interested_you),
                stringResource(R.string.interested_you_details)
            ) { callback?.onBack() }
            Column(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 20.dp)
            ) {
                CategoryRow(list[1])
                CategoryRow(list[2])
                CategoryRow(list[0])
            }
        }
        GradientButton(
            { callback?.onNext() },
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.next_button),
            true
        )
    }
}

@Composable
private fun CategoryRow(items: List<CategoryModel>) {
    Row {
        for (item in items) {
            val iconState = remember { mutableStateOf(false) }
            val stateColor = if (iconState.value) Color(item.color.toColorInt())
            else ThemeExtra.colors.grayIcon
            val position = if (items.indexOf(item) % 2 == 0) Alignment.TopCenter
            else Alignment.BottomCenter
            Box(
                Modifier
                    .width(150.dp)
                    .height(170.dp), position
            ) {
                Box {
                    Box(
                        Modifier
                            .width(140.dp)
                            .height(140.dp)
                            .clip(CircleShape)
                            .background(stateColor)
                            .align(Alignment.BottomCenter)
                            .clickable { iconState.value = !iconState.value },
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
                            .width(50.dp)
                            .height(50.dp)
                            .clip(CircleShape)
                            .background(ThemeExtra.colors.white)
                            .align(Alignment.BottomStart),
                        Alignment.Center
                    ) {
                        AsyncImage(
                            item.emoji.path,
                            stringResource(R.string.next_button),
                            Modifier.size(22.dp),
                            painterResource(R.drawable.cinema)
                        )
                    }
                }
            }
        }
    }
}

private fun listSeparator(categories: List<CategoryModel>): List<List<CategoryModel>> {
    // Сепаратор разделяет список категорий на три строки
    val first = arrayListOf<CategoryModel>()
    val second = arrayListOf<CategoryModel>()
    val third = arrayListOf<CategoryModel>()
    for (category in categories) when {
        categories.indexOf(category) % 3 == 0 -> first.add(category)
        categories.indexOf(category) % 2 == 0 -> second.add(category)
        else -> third.add(category)
    }
    return listOf(first, second, third)
}

@Composable
@ExperimentalMaterial3Api
@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
private fun SelectCategoriesPreview() {
    SelectCategories(
        Modifier,
        SelectCategoriesState(DemoCategoryModelList)
    )
}