package ru.rikmasters.gilty.presentation.model.meeting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.shared.GiltyChip
import ru.rikmasters.gilty.presentation.ui.shared.TrackCheckBox
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

data class FilterModel(
    val name: String,
    val content: @Composable () -> Unit
)

val ListOfFilters = listOf(
    FilterModel("Категория") {
        Category(
            arrayListOf(DemoCategoryModel, DemoCategoryModel, DemoCategoryModel),
            onCategoryClick = {},
            onAllCategoryClick = {}
        )
    },
    FilterModel("Поиск по тегам") { TagSearch { } },
    FilterModel("Расстояние") { Distance(18) { } },
    FilterModel("Тип встречи") { MeetingType(true) { } },
    FilterModel("Пол и условия") { GenderAndConditions { } }
)

@Composable
private fun Category(
    categories: ArrayList<CategoryModel>,
    onCategoryClick: (selected: Int) -> Unit,
    onAllCategoryClick: () -> Unit
) {
    categories.forEachIndexed { index, category ->
        Card(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { onCategoryClick(index) },
            MaterialTheme.shapes.large,
            CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp), SpaceBetween
            ) {
                Row {
                    AsyncImage(
                        category.emoji.path,
                        null,
                        Modifier.size(20.dp),
                        placeholder = painterResource(R.drawable.cinema)
                    )
                    Text(
                        category.name,
                        Modifier.padding(start = 18.dp),
                        ThemeExtra.colors.mainTextColor,
                        style = ThemeExtra.typography.Body1Sb
                    )
                }
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    stringResource(R.string.next_button),
                    tint = ThemeExtra.colors.secondaryTextColor
                )
            }
        }
    }
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { onAllCategoryClick() },
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), SpaceBetween
        ) {
            Text(
                "Показать все категории",
                color = ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.Body1Sb
            )
            Icon(
                Icons.Filled.KeyboardArrowRight,
                stringResource(R.string.next_button),
                tint = ThemeExtra.colors.secondaryTextColor
            )
        }
    }
}

@Composable
private fun TagSearch(onClick: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                painterResource(R.drawable.magnifier),
                stringResource(R.string.login_search_placeholder),
                Modifier.size(20.dp),
                ThemeExtra.colors.secondaryTextColor
            )
            Text(
                stringResource(R.string.meeting_filter_add_tag_text_holder),
                Modifier.padding(start = 12.dp),
                ThemeExtra.colors.secondaryTextColor,
                style = ThemeExtra.typography.Body1Medium
            )
        }
    }
}

@Composable
private fun Distance(distance: Int, onClick: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp), SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.meeting_filter_radius_of_search_label),
                Modifier.padding(start = 16.dp),
                ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.Body1Sb
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "$distance км",
                        Modifier.padding(12.dp, 6.dp),
                        Color.White,
                        style = ThemeExtra.typography.SubHeadSb
                    )
                }
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    stringResource(R.string.next_button),
                    Modifier.padding(horizontal = 16.dp),
                    ThemeExtra.colors.secondaryTextColor
                )
            }
        }
    }
}

@Composable
private fun MeetingType(hiddenPhoto: Boolean, onClick: (Boolean) -> Unit) {
    val list = remember { mutableStateListOf(false, false, false) }
    Card(
        Modifier.fillMaxWidth(),
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
    ) {
        Row {
            list.forEachIndexed { index, it ->
                GiltyChip(
                    Modifier
                        .padding(vertical = 12.dp)
                        .padding(start = 12.dp),
                    stringResource(
                        listOf(
                            R.string.meeting_filter_select_meeting_type_personal,
                            R.string.meeting_filter_select_meeting_type_grouped,
                            R.string.meeting_filter_select_meeting_type_anonymous
                        )[index]
                    ), it
                ) {
                    for (i in 0..list.lastIndex) list[i] = false
                    list[index] = true
                }
            }
        }
    }
    Card(
        Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(),
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.meeting_only_online_meetings_button))
            TrackCheckBox(hiddenPhoto) { onClick(it) }
        }
    }
}

@Composable
private fun GenderAndConditions(onClick: () -> Unit) {
    val genderList = remember { mutableStateListOf(false, false, false) }
    val conditionsList = remember { mutableStateListOf(false, false, false, false, false) }
    Card(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
    ) {
        Row {
            genderList.forEachIndexed { index, it ->
                GiltyChip(
                    Modifier
                        .padding(vertical = 12.dp)
                        .padding(start = 12.dp),
                    stringResource(
                        listOf(
                            R.string.meeting_filter_select_meeting_type_personal,
                            R.string.meeting_filter_select_meeting_type_grouped,
                            R.string.meeting_filter_select_meeting_type_anonymous
                        )[index]
                    ), it
                ) {
                    for (i in 0..genderList.lastIndex) genderList[i] = false
                    genderList[index] = true
                }
            }
        }
    }
    Card(
        Modifier.fillMaxWidth(),
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
    ) {
        LazyVerticalGrid(
            GridCells.Fixed(3),
            Modifier.height(120.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(conditionsList) { index, it ->
                GiltyChip(
                    Modifier,
                    stringResource(
                        listOf(
                            R.string.female_sex,
                            R.string.male_sex,
                            R.string.others_sex,
                            R.string.male_sex,
                            R.string.others_sex
                        )[index]
                    ), it
                ) {
                    for (i in 0..conditionsList.lastIndex) conditionsList[i] = false
                    conditionsList[index] = true
                }
            }
        }
    }
}