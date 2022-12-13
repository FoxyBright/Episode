package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.FullCategoryModel
import ru.rikmasters.gilty.shared.shared.CheckBoxCard
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.GiltyChip
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.Gradients.red

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
            shapes.large,
            cardColors(colorScheme.primaryContainer),
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
                        colorScheme.tertiary,
                        style = typography.bodyMedium,
                        fontWeight = SemiBold
                    )
                }
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    stringResource(R.string.next_button),
                    tint = colorScheme.onTertiary
                )
            }
            category.subcategories?.let {
                if(categoryStatus[index]) {
                    Divider()
                    FlowLayout(
                        Modifier
                            .background(colorScheme.primaryContainer)
                            .padding(top = 16.dp)
                            .padding(horizontal = 8.dp), 8.dp, 8.dp
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
        shapes.large,
        cardColors(colorScheme.primaryContainer),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), Arrangement.Absolute.SpaceBetween
        ) {
            Text(
                stringResource(R.string.meeting_filter_show_all_categories),
                color = colorScheme.tertiary,
                style = typography.bodyMedium,
                fontWeight = SemiBold
            )
            Icon(
                Icons.Filled.KeyboardArrowRight,
                stringResource(R.string.next_button),
                tint = colorScheme.onTertiary
            )
        }
    }
}

@Composable
fun TagSearch(
    tagList: List<String>,
    onClick: () -> Unit,
    online: Boolean = false,
    onDeleteTag: (Int) -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shapes.large,
        cardColors(colorScheme.primaryContainer),
    ) {
        if(tagList.isEmpty())
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    painterResource(R.drawable.magnifier),
                    stringResource(R.string.login_search_placeholder),
                    Modifier.size(20.dp),
                    colorScheme.onTertiary
                )
                Text(
                    stringResource(R.string.meeting_filter_add_tag_text_holder),
                    Modifier.padding(start = 12.dp),
                    colorScheme.onTertiary,
                    style = typography.bodyMedium,
                    fontWeight = Bold
                )
            }
        else Surface {
            FlowLayout(
                Modifier
                    .background(colorScheme.primaryContainer)
                    .padding(top = 8.dp)
                    .padding(horizontal = 8.dp), 8.dp, 8.dp
            ) {
                tagList.forEachIndexed { index, item ->
                    Box(
                        Modifier
                            .clip(shapes.large)
                            .background(linearGradient(if(online) green() else red()))
                    ) {
                        Row(
                            Modifier.padding(12.dp, 6.dp),
                            Arrangement.Center, CenterVertically
                        ) {
                            Text(
                                item,
                                Modifier.padding(end = 10.dp),
                                Color.White,
                                style = typography.labelSmall
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
        shapes.large,
        cardColors(colorScheme.primaryContainer),
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
                colorScheme.tertiary,
                style = typography.bodyMedium,
                fontWeight = SemiBold
            )
            Row(verticalAlignment = CenterVertically) {
                Box(
                    Modifier
                        .clip(shapes.extraSmall)
                        .background(colorScheme.primary)
                ) {
                    Text(
                        stringResource(R.string.meeting_filter_label_distance, distance),
                        Modifier.padding(12.dp, 6.dp),
                        Color.White,
                        style = typography.labelSmall,
                        fontWeight = SemiBold
                    )
                }
                Icon(
                    if(state) Icons.Filled.KeyboardArrowDown
                    else Icons.Filled.KeyboardArrowRight,
                    stringResource(R.string.next_button),
                    Modifier.padding(horizontal = 16.dp),
                    colorScheme.onTertiary
                )
            }
        }
        if(state) {
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
                        color = colorScheme.tertiary,
                        style = typography.bodyMedium,
                        fontWeight = SemiBold
                    )
                    Text(
                        stringResource(R.string.meeting_filter_label_distance, 50),
                        color = colorScheme.tertiary,
                        style = typography.bodyMedium,
                        fontWeight = SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun MeetingType(
    checkState: Boolean,
    selectedMeetingType: List<Boolean>,
    CheckLabel: String,
    online: Boolean = false,
    onOnlyOnlineClick: (Boolean) -> Unit,
    onMeetingTypeSelect: (Int, Boolean) -> Unit
) {
    val typeList = listOf(
        stringResource(R.string.meeting_filter_select_meeting_type_personal),
        stringResource(R.string.meeting_filter_select_meeting_type_grouped),
        stringResource(R.string.meeting_filter_select_meeting_type_anonymous)
    )
    Card(
        Modifier.fillMaxWidth(), shapes.large,
        cardColors(colorScheme.primaryContainer),
    ) {
        Surface {
            FlowLayout(
                Modifier
                    .background(colorScheme.primaryContainer)
                    .padding(top = 8.dp)
                    .padding(8.dp), 8.dp, 8.dp
            ) {
                selectedMeetingType.forEachIndexed { index, item ->
                    GiltyChip(
                        Modifier, typeList[index], item, online
                    ) { onMeetingTypeSelect(index, item) }
                }
            }
        }
    }
    CheckBoxCard(
        CheckLabel,
        Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(), checkState,
        online = online
    ) { onOnlyOnlineClick(it) }
}

@Composable
fun ConditionsSelect(
    selectedMeetingTypes: List<Boolean>,
    online: Boolean = false,
    onConditionSelect: (Int, Boolean) -> Unit
) {
    val conditionList = listOf(
        stringResource(R.string.meeting_filter_select_meeting_type_free),
        stringResource(R.string.meeting_filter_select_meeting_type_divide_amount),
        stringResource(R.string.meeting_filter_select_meeting_type_organizer_pays),
        stringResource(R.string.meeting_filter_select_meeting_type_paid),
        stringResource(R.string.meeting_filter_select_meeting_type_does_not_matter)
    )
    Card(
        Modifier.fillMaxWidth(), shapes.large,
        cardColors(colorScheme.primaryContainer),
    ) {
        Surface {
            FlowLayout(
                Modifier
                    .background(colorScheme.primaryContainer)
                    .padding(top = 8.dp)
                    .padding(8.dp), 8.dp, 8.dp
            ) {
                selectedMeetingTypes.forEachIndexed { index, item ->
                    GiltyChip(
                        Modifier,
                        conditionList[index],
                        item, online
                    ) { onConditionSelect(index, item) }
                }
            }
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
        shapes.large,
        cardColors(colorScheme.primaryContainer),
    ) {
        Surface {
            FlowLayout(
                Modifier
                    .background(colorScheme.primaryContainer)
                    .padding(top = 8.dp)
                    .padding(8.dp), 8.dp, 8.dp
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
        shapes.large,
        cardColors(colorScheme.primaryContainer),
    ) {
        Surface {
            FlowLayout(
                Modifier
                    .background(colorScheme.primaryContainer)
                    .padding(top = 8.dp)
                    .padding(8.dp), 8.dp, 8.dp
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
