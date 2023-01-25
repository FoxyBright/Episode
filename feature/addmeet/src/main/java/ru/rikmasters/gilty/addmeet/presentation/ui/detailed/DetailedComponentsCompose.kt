package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.TagSearch
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.*

@Composable
fun tags(
    state: DetailedState,
    callback: DetailedCallback? = null,
) = FilterModel(
    stringResource(R.string.add_meet_detailed_tags),
    stringResource(R.string.add_meet_detailed_tags_maximum, (3))
) {
    Column {
        TagSearch(
            state.tagList,
            { callback?.onTagsClick() },
            state.online
        ) { callback?.onTagDelete(state.tagList[it]) }
        Text(
            stringResource(R.string.add_meet_detailed_tags_description),
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 16.dp),
            colorScheme.onTertiary,
            style = typography.headlineSmall
        )
    }
}

@Composable
fun description(
    state: DetailedState,
    callback: DetailedCallback? = null,
) = FilterModel(stringResource(R.string.add_meet_detailed_meet_description)) {
    Column {
        Column(Modifier.fillMaxWidth()) {
            GTextField(
                state.description, {
                    if(it.length <= 120)
                        callback?.onDescriptionChange(it)
                },
                Modifier.fillMaxWidth(),
                shape = shapes.medium,
                colors = DescriptionColors(state.online),
                label = if(state.description.isNotEmpty())
                    TextFieldLabel(
                        true,
                        stringResource(R.string.add_meet_detailed_meet_description_place_holder)
                    ) else null, placeholder = TextFieldLabel(
                    false,
                    stringResource(R.string.add_meet_detailed_meet_description_place_holder)
                ), textStyle = typography.bodyMedium,
                clear = { callback?.onDescriptionClear() }
            )
            Text(
                "${state.description.length}/120",
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colorScheme.onTertiary,
                textAlign = TextAlign.End,
                style = typography.headlineSmall
            )
        }
    }
}

@Composable
fun additionally(
    state: DetailedState,
    callback: DetailedCallback? = null,
) = FilterModel(stringResource(R.string.add_meet_conditions_additionally)) {
    Row(Modifier.fillMaxWidth()) {
        DataTimeCard(
            state.date, DataTimeType.DATE,
            state.online, Modifier.weight(1f)
        ) { callback?.onDateClick() }
        Spacer(Modifier.width(16.dp))
        DataTimeCard(
            state.time, DataTimeType.TIME,
            state.online, Modifier.weight(1f)
        ) { callback?.onTimeClick() }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MeetPlace(
    state: DetailedState,
    modifier: Modifier = Modifier,
    callback: DetailedCallback? = null,
) {
    Column {
        Column(modifier.fillMaxWidth()) {
            Card(
                { callback?.onMeetPlaceClick() },
                Modifier.fillMaxWidth(), (true), shapes.medium,
                cardColors(colorScheme.primaryContainer)
            ) {
                state.meetPlace?.let {
                    Column(Modifier.fillMaxWidth()) {
                        state.meetPlace
                        Text(
                            it.first, Modifier
                                .padding(top = 8.dp)
                                .padding(horizontal = 16.dp),
                            colorScheme.onTertiary,
                            style = typography.headlineSmall
                        )
                        Text(
                            it.second, Modifier
                                .padding(top = 2.dp, bottom = 10.dp)
                                .padding(horizontal = 16.dp),
                            colorScheme.tertiary,
                            style = typography.bodyMedium
                        )
                    }
                } ?: run {
                    Text(
                        stringResource(R.string.add_meet_detailed_meet_place),
                        Modifier.padding(16.dp),
                        colorScheme.onTertiary,
                        style = typography.bodyMedium
                    )
                }
            }
        }
        Text(
            stringResource(R.string.add_meet_detailed_meet_place_place_holder),
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 16.dp),
            colorScheme.onTertiary,
            style = typography.headlineSmall
        )
    }
}

@Composable
fun HideMeetPlace(
    state: DetailedState,
    modifier: Modifier = Modifier,
    callback: DetailedCallback? = null,
) {
    CheckBoxCard(
        stringResource(R.string.add_meet_detailed_meet_hide_place),
        modifier.fillMaxWidth(), state.hideMeetPlace,
        online = state.online
    ) { callback?.onHideMeetPlaceClick() }
}