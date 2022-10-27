package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.mainscreen.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FullCategoryModel
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.shared.TrackCheckBox
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun Category(
    categories: List<FullCategoryModel>,
    categoryStatus: List<Boolean>,
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
                    .padding(16.dp), Arrangement.Absolute.SpaceBetween, CenterVertically
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
            category.subcategories?.let {
                if (categoryStatus[index]) {
                    Divider()
                    Surface {
                        FlowLayout(
                            Modifier
                                .background(ThemeExtra.colors.cardBackground)
                                .padding(top = 16.dp)
                                .padding(horizontal = 16.dp), 8.dp, 8.dp
                        ) {
                            it.forEach { category ->
                                GiltyChip(
                                    Modifier,
                                    category,
                                    false
                                ) { //TODO: Выбор подкатегорий }
                                }
                            }
                        }
                    }
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
                    .padding(16.dp), Arrangement.Absolute.SpaceBetween
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
}

@Composable
fun TagSearch(
    tagList: List<String>,
    onClick: () -> Unit,
    onDeleteTag: (Int) -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
    ) {
        if (tagList.isEmpty())
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
        else
            Surface {
                FlowLayout(
                    Modifier
                        .background(ThemeExtra.colors.cardBackground)
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp), 8.dp, 8.dp
                ) {
                    tagList.forEachIndexed { index, item ->
                        Box(
                            Modifier
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Row(
                                Modifier.padding(12.dp, 6.dp),
                                Arrangement.Center, CenterVertically
                            ) {
                                Text(
                                    item,
                                    Modifier.padding(end = 10.dp),
                                    Color.White,
                                    style = ThemeExtra.typography.MediumText
                                )
                                Icon(
                                    Icons.Filled.Close,
                                    stringResource(R.string.meeting_filter_delete_tag_label),
                                    Modifier.clickable { onDeleteTag(index) },
                                    Color.White
                                )
                            }
                        }
                    }
                }
            }
    }
}


@Composable
fun Distance(
    distance: Int,
    state: Boolean,
    onClick: () -> Unit,
    onValueChange: (value: Int) -> Unit
) {
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
                .padding(vertical = 10.dp), Arrangement.Absolute.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Text(
                stringResource(R.string.meeting_filter_radius_of_search_label),
                Modifier.padding(start = 16.dp),
                ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.Body1Sb
            )
            Row(verticalAlignment = CenterVertically) {
                Box(
                    Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        stringResource(R.string.meeting_filter_label_distance, distance),
                        Modifier.padding(12.dp, 6.dp),
                        Color.White,
                        style = ThemeExtra.typography.SubHeadSb
                    )
                }
                Icon(
                    if (state) Icons.Filled.KeyboardArrowDown
                    else Icons.Filled.KeyboardArrowRight,
                    stringResource(R.string.next_button),
                    Modifier.padding(horizontal = 16.dp),
                    ThemeExtra.colors.secondaryTextColor
                )
            }
        }
        if (state) {
            Divider()
            Column {
                Slider(
                    distance.toFloat(),
                    { onValueChange(it.toInt()) },
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 50.dp),
                    valueRange = 1f..50f, steps = 50,
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 18.dp),
                    Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.meeting_filter_label_distance, 1),
                        color = ThemeExtra.colors.mainTextColor,
                        style = ThemeExtra.typography.Body1Sb
                    )
                    Text(
                        stringResource(R.string.meeting_filter_label_distance, 50),
                        color = ThemeExtra.colors.mainTextColor,
                        style = ThemeExtra.typography.Body1Sb
                    )
                }
            }
        }
    }
}


@Composable
fun MeetingType(
    hiddenPhoto: Boolean,
    selectedMeetingType: List<Boolean>,
    onOnlyOnlineClick: (Boolean) -> Unit,
    onMeetingTypeSelect: (Int, Boolean) -> Unit
) {
    val typeList = listOf(
        stringResource(R.string.meeting_filter_select_meeting_type_personal),
        stringResource(R.string.meeting_filter_select_meeting_type_grouped),
        stringResource(R.string.meeting_filter_select_meeting_type_anonymous)
    )
    Card(
        Modifier.fillMaxWidth(),
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
    ) {
        Surface {
            FlowLayout(
                Modifier
                    .background(ThemeExtra.colors.cardBackground)
                    .padding(top = 8.dp, start = 16.dp), 8.dp, 8.dp
            ) {
                selectedMeetingType.forEachIndexed { index, item ->
                    GiltyChip(
                        Modifier,
                        typeList[index],
                        item
                    ) { onMeetingTypeSelect(index, item) }
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
            CenterVertically
        ) {
            Text(stringResource(R.string.meeting_only_online_meetings_button))
            TrackCheckBox(hiddenPhoto) { onOnlyOnlineClick(it) }
        }
    }
}


@Composable
fun GenderAndConditions(
    selectedGenders: List<Boolean>,
    selectedMeetingTypes: List<Boolean>,
    onGenderSelect: (Int, Boolean) -> Unit,
    onConditionSelect: (Int, Boolean) -> Unit
) {
    val genderList = listOf(
        stringResource(R.string.female_sex),
        stringResource(R.string.male_sex),
        stringResource(R.string.others_sex)
    )
    val conditionList = listOf(
        stringResource(R.string.meeting_filter_select_meeting_type_free),
        stringResource(R.string.meeting_filter_select_meeting_type_divide_amount),
        stringResource(R.string.meeting_filter_select_meeting_type_organizer_pays),
        stringResource(R.string.meeting_filter_select_meeting_type_paid),
        stringResource(R.string.meeting_filter_select_meeting_type_does_not_matter)
    )
    Card(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
    ) {
        Surface {
            FlowLayout(
                Modifier
                    .background(ThemeExtra.colors.cardBackground)
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp), 8.dp, 8.dp
            ) {
                selectedGenders.forEachIndexed { index, item ->
                    GiltyChip(
                        Modifier,
                        genderList[index],
                        item
                    ) { onGenderSelect(index, item) }
                }
            }
        }
    }
    Card(
        Modifier.fillMaxWidth(),
        MaterialTheme.shapes.large,
        CardDefaults.cardColors(ThemeExtra.colors.cardBackground),
    ) {
        Surface {
            FlowLayout(
                Modifier
                    .background(ThemeExtra.colors.cardBackground)
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp), 8.dp, 8.dp
            ) {
                selectedMeetingTypes.forEachIndexed { index, item ->
                    GiltyChip(
                        Modifier,
                        conditionList[index],
                        item
                    ) { onConditionSelect(index, item) }
                }
            }
        }
    }
}
