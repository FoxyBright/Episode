package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SliderDefaults.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.FlowLayout
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.magnifier
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.Gradients.red

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Category(
    categories: List<CategoryModel>,
    states: List<Int>,
    selected: List<CategoryModel>,
    onCategoryClick: (Int, CategoryModel) -> Unit,
    onSubClick: (CategoryModel) -> Unit,
    onAllCategoryClick: () -> Unit,
) {
    categories.forEachIndexed { index, category ->
        Card(
            { onCategoryClick(index, category) },
            Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = shapes.large,
            colors = cardColors(colorScheme.primaryContainer),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                SpaceBetween, CenterVertically
            ) {
                Row(Modifier, Start, CenterVertically) {
                    GEmojiImage(
                        category.emoji,
                        Modifier.size(20.dp)
                    )
                    Text(
                        category.name,
                        Modifier
                            .padding(start = 18.dp)
                            .padding(vertical = 16.dp),
                        colorScheme.tertiary,
                        style = typography.bodyMedium,
                        fontWeight = SemiBold
                    )
                }
                if(!category.children.isNullOrEmpty()) Icon(
                    Filled.KeyboardArrowRight, (null),
                    Modifier, colorScheme.onTertiary
                )
                else if(selected.contains(category)) Image(
                    painterResource(R.drawable.enabled_check_box),
                    (null), Modifier.size(32.dp)
                )
            }
            if(!category.children.isNullOrEmpty()
                && states.contains(index)
            ) {
                GDivider(); FlowLayout(
                    Modifier
                        .background(colorScheme.primaryContainer)
                        .padding(top = 16.dp)
                        .padding(horizontal = 8.dp), 8.dp, 8.dp
                ) {
                    category.children.forEach { sub ->
                        GiltyChip(
                            Modifier, sub.name,
                            selected.contains(sub)
                        ) { onSubClick(sub) }
                    }
                }
            }
        }
    }
    Card(
        Modifier
            .clickable {
                onAllCategoryClick()
            },
        shapes.large,
        cardColors(colorScheme.primaryContainer),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            SpaceBetween, CenterVertically
        ) {
            Text(
                stringResource(R.string.meeting_filter_show_all_categories),
                color = colorScheme.tertiary,
                style = typography.bodyMedium,
                fontWeight = SemiBold
            )
            Icon(
                Filled.KeyboardArrowRight,
                stringResource(R.string.next_button),
                tint = colorScheme.onTertiary
            )
        }
    }
}

@Composable
fun TagSearch(
    tagList: List<TagModel>,
    onClick: () -> Unit,
    online: Boolean = false,
    onDeleteTag: (TagModel) -> Unit,
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
                    painterResource(magnifier),
                    stringResource(R.string.search_placeholder),
                    Modifier.size(20.dp),
                    colorScheme.scrim
                )
                Text(
                    stringResource(R.string.meeting_filter_add_tag_text_holder),
                    Modifier.padding(start = 12.dp),
                    colorScheme.scrim,
                    style = typography.bodyMedium,
                    fontWeight = W700
                )
            }
        else FlowLayout(
            Modifier
                .background(colorScheme.primaryContainer)
                .padding(top = 8.dp)
                .padding(horizontal = 8.dp), 8.dp, 8.dp
        ) {
            tagList.forEach {
                Box(
                    Modifier
                        .clip(shapes.large)
                        .background(linearGradient(if(online) green() else red()))
                ) {
                    Row(
                        Modifier
                            .width(Max)
                            .padding(12.dp, 6.dp),
                        Arrangement.Center, CenterVertically
                    ) {
                        Text(
                            it.title,
                            Modifier
                                .weight(1f)
                                .padding(end = 10.dp),
                            Color.White,
                            style = typography.labelSmall
                        )
                        Icon(
                            Filled.Close, (null),
                            Modifier.clickable { onDeleteTag(it) },
                            Color.White
                        )
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
    onValueChange: (value: Int) -> Unit,
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
                .padding(vertical = 10.dp), SpaceBetween,
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
                        stringResource(
                            R.string.meeting_filter_label_distance,
                            distance
                        ),
                        Modifier.padding(12.dp, 6.dp),
                        Color.White,
                        style = typography.labelSmall,
                        fontWeight = SemiBold
                    )
                }
                Icon(
                    if(state) Filled.KeyboardArrowDown
                    else Filled.KeyboardArrowRight,
                    stringResource(R.string.next_button),
                    Modifier.padding(horizontal = 16.dp),
                    colorScheme.onTertiary
                )
            }
        }
        if(state) {
            GDivider()
            Column {
                Slider(
                    distance.toFloat(),
                    { onValueChange(it.toInt()) },
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 50.dp),
                    valueRange = 1f..50f, steps = 50,
                    colors = colors(
                        activeTickColor = Transparent,
                        inactiveTickColor = Transparent,
                        inactiveTrackColor = colorScheme.outline
                    )
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 18.dp),
                    SpaceBetween
                ) {
                    Text(
                        stringResource(
                            R.string.meeting_filter_label_distance,
                            1
                        ),
                        color = colorScheme.tertiary,
                        style = typography.bodyMedium,
                        fontWeight = SemiBold
                    )
                    Text(
                        stringResource(
                            R.string.meeting_filter_label_distance,
                            50
                        ),
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
    selected: List<Int>,
    checkLabel: String,
    online: Boolean = false,
    onOnlyOnlineClick: (Boolean) -> Unit,
    onMeetingTypeSelect: (Int) -> Unit,
) {
    val types = MeetType.list
    Card(
        Modifier.fillMaxWidth(), shapes.large,
        cardColors(colorScheme.primaryContainer),
    ) {
        FlowLayout(
            Modifier
                .background(colorScheme.primaryContainer)
                .padding(top = 8.dp)
                .padding(8.dp), 8.dp, 8.dp
        ) {
            repeat(types.size) {
                GiltyChip(
                    Modifier, types[it].displayShort,
                    selected.contains(it), online
                ) { onMeetingTypeSelect(it) }
            }
        }
    }
    CheckBoxCard(
        checkLabel,
        Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(), checkState,
        online = online
    ) { onOnlyOnlineClick(it) }
}

@Composable
fun ConditionsSelect(
    selected: List<Int>,
    online: Boolean = false,
    onConditionSelect: (Int) -> Unit,
) {
    val conditions = ConditionType.list
    Card(
        Modifier.fillMaxWidth(), shapes.large,
        cardColors(colorScheme.primaryContainer),
    ) {
        FlowLayout(
            Modifier
                .background(colorScheme.primaryContainer)
                .padding(top = 8.dp)
                .padding(8.dp), 8.dp, 8.dp
        ) {
            repeat(conditions.size) {
                GiltyChip(
                    Modifier,
                    conditions[it].display,
                    selected.contains(it), online
                ) { onConditionSelect(it) }
            }
        }
    }
}

@Composable
fun Conditions(
    selectedConditions: List<Int>,
    onConditionSelect: (Int) -> Unit,
) {
    Card(
        Modifier.fillMaxWidth(),
        shapes.large,
        cardColors(colorScheme.primaryContainer),
    ) {
        FlowLayout(
            Modifier
                .background(colorScheme.primaryContainer)
                .padding(top = 8.dp)
                .padding(8.dp), 8.dp, 8.dp
        ) {
            repeat(ConditionType.list.size) {
                GiltyChip(
                    Modifier,
                    ConditionType.list[it].display,
                    selectedConditions.contains(it)
                ) { onConditionSelect(it) }
            }
        }
    }
}
