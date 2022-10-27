package ru.rikmasters.gilty.mainscreen.presentation.ui.categories

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.mainscreen.custom.FlowLayout
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoFullCategoryModelList
import ru.rikmasters.gilty.shared.model.meeting.FullCategoryModel
import ru.rikmasters.gilty.shared.shared.CheckBox
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun CategoryListPreview() {
    GiltyTheme {
        CategoryList(
            CategoryListState(
                DemoFullCategoryModelList,
                listOf()
            ), Modifier.padding(16.dp)
        )
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

@OptIn(ExperimentalMaterial3Api::class)
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
                Modifier
                    .clip(CircleShape)
                    .clickable { callback?.onBack() },
                ThemeExtra.colors.mainTextColor
            )
            Text(
                stringResource(R.string.meeting_filter_select_categories),
                Modifier.padding(start = 20.dp),
                ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.H3
            )
        }
        LazyColumn {
            itemsIndexed(state.categoryList)
            { index, it ->
                Card(
                    {
                        callback?.onCategoryClick(
                            index, state.categoryListState[index]
                        )
                    }, Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp), true,
                    MaterialTheme.shapes.large,
                    CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
                )
                {
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
                                if (!state.categoryListState[index])
                                    Icons.Filled.KeyboardArrowRight
                                else Icons.Filled.KeyboardArrowDown,
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
                                it.subcategories!!.forEach {
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
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 28.dp),
                    stringResource(R.string.meeting_filter_complete_button)
                ) { callback?.onNext() }
            }
            item {
                Text(
                    stringResource(R.string.meeting_filter_clear),
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 28.dp)
                        .clip(CircleShape)
                        .clickable { },
                    ThemeExtra.colors.mainTextColor,
                    textAlign = TextAlign.Center,
                    style = ThemeExtra.typography.Body2Bold
                )
            }
        }
    }
}