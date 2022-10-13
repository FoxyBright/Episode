package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.DemoFullCategoryModelList
import ru.rikmasters.gilty.presentation.model.meeting.FullCategoryModel
import ru.rikmasters.gilty.presentation.ui.presentation.custom.FlowLayout
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.shared.CheckBox
import ru.rikmasters.gilty.presentation.ui.shared.Divider
import ru.rikmasters.gilty.presentation.ui.shared.GiltyChip
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview
@Composable
fun CategoryListPreview() {
    GiltyTheme {
        val list = remember { mutableStateListOf<Boolean>() }
        repeat(DemoFullCategoryModelList.size) { list.add(false) }
        CategoryList(
            CategoryListState(DemoFullCategoryModelList, list), Modifier.padding(16.dp),
            object : CategoryListCallback {
                override fun onCategoryClick(index: Int, it: Boolean) {
                    list[index] = !it
                }
            })
    }
}

interface CategoryListCallback : NavigationInterface {
    fun onCategoryClick(index: Int, it: Boolean)
    fun onCategorySelect() {}
}

data class CategoryListState(
    val categoryList: List<FullCategoryModel>,
    val categoryListState: List<Boolean>
)

@Composable
fun CategoryList(
    state: CategoryListState,
    modifier: Modifier = Modifier,
    callback: CategoryListCallback? = null
) {
    Column(modifier) {
        Row(
            Modifier.padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.ic_back),
                stringResource(R.string.action_bar_button_back),
                Modifier.clickable { callback?.onBack() },
                ThemeExtra.colors.mainTextColor
            )
            Text(
                "Выбрать категории",
                Modifier.padding(start = 20.dp),
                ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.H3
            )
        }
        LazyColumn {
            itemsIndexed(state.categoryList) { index, it ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            callback?.onCategoryClick(
                                index,
                                state.categoryListState[index]
                            )
                        },
                    MaterialTheme.shapes.large,
                    CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        Arrangement.Absolute.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Row {
                            AsyncImage(
                                it.emoji.path,
                                null,
                                Modifier.size(20.dp),
                                placeholder = painterResource(R.drawable.cinema)
                            )
                            Text(
                                it.name,
                                Modifier.padding(start = 18.dp),
                                ThemeExtra.colors.mainTextColor,
                                style = ThemeExtra.typography.Body1Sb
                            )
                        }
                        if (it.subcategories != null)
                            Icon(
                                Icons.Filled.KeyboardArrowRight,
                                stringResource(R.string.next_button),
                                tint = ThemeExtra.colors.secondaryTextColor
                            )
                        else if (state.categoryListState[index]) CheckBox(true) {}
                    }
                    if (it.subcategories != null && state.categoryListState[index]) {
                        Divider()
                        Surface {
                            FlowLayout(
                                Modifier
                                    .background(ThemeExtra.colors.cardBackground)
                                    .padding(top = 16.dp)
                                    .padding(horizontal = 16.dp), 8.dp, 8.dp
                            ) {
                                it.subcategories.forEach {
                                    GiltyChip(
                                        Modifier,
                                        it,
                                        false
                                    ) {
                                        callback?.onCategorySelect()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                GradientButton(
                    { callback?.onNext() },
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 28.dp),
                    stringResource(R.string.meeting_filter_complete_button)
                )
            }
            item {
                Text(
                    stringResource(R.string.meeting_filter_clear),
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 28.dp)
                        .clickable { },
                    ThemeExtra.colors.mainTextColor,
                    textAlign = TextAlign.Center,
                    style = ThemeExtra.typography.Body2Bold
                )
            }
        }
    }
}