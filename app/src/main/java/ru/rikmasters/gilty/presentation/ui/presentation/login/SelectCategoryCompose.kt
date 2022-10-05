package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
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
import ru.rikmasters.gilty.presentation.ui.shared.ActionBar
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

data class SelectCategoriesState(
    val categoryList: List<List<CategoryModel>>
)

@Composable
@ExperimentalMaterial3Api
fun SelectCategories(
    modifier: Modifier,
    state: SelectCategoriesState,
    callback: NavigationInterface? = null
) {
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
            ActionBar(
                stringResource(R.string.interested_you),
                stringResource(R.string.interested_you_details)
            ) { callback?.onBack() }
            LazyHorizontalGrid(
                GridCells.Fixed(3),
                Modifier
                    .height(550.dp)
                    .padding(top = 10.dp)
            ) {
                items(state.categoryList.size) {
                    Row {
                        for (item in state.categoryList[it]) {
                            val iconState = remember { mutableStateOf(false) }
                            Box(
                                Modifier
                                    .width(150.dp)
                                    .height(170.dp),
                                if (state.categoryList[it].indexOf(item) % 2 == 0) Alignment.TopCenter
                                else Alignment.BottomCenter
                            ) {
                                Box {
                                    Box(
                                        Modifier
                                            .width(140.dp)
                                            .height(140.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (iconState.value) Color(item.color.toColorInt())
                                                else ThemeExtra.colors.grayIcon
                                            )
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
                                            .background(Color.White)
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
            SelectCategoriesState(CategoriesListSeparator(DemoCategoryModelList))
        )
    }
}