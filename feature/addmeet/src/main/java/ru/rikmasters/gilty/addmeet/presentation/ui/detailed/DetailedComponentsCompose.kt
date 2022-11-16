package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.TagSearch
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.CheckBoxCard
import ru.rikmasters.gilty.shared.shared.GTextField
import ru.rikmasters.gilty.shared.shared.TextFieldColors
import ru.rikmasters.gilty.shared.shared.TextFieldLabel

@Composable
fun Tags(
    state: DetailedState,
    callback: DetailedCallback? = null
): FilterModel {
    return FilterModel(
        stringResource(R.string.add_meet_detailed_tags),
        stringResource(R.string.add_meet_detailed_tags_maximum, (3))
    ) {
        Column {
            TagSearch(state.tagList, { callback?.onTagsClick() })
            { callback?.onTagDelete(it) }
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
}

@Composable
fun Description(
    state: DetailedState,
    callback: DetailedCallback? = null
): FilterModel {
    return FilterModel(stringResource(R.string.add_meet_detailed_meet_description)) {
        Column {
            Column(Modifier.fillMaxWidth()) {
                GTextField(
                    state.description, {
                        if (it.length <= 120)
                            callback?.onDescriptionChange(it)
                    },
                    Modifier.fillMaxWidth(),
                    shape = shapes.medium,
                    colors = TextFieldColors(),
                    label = if (state.description.isNotEmpty()) TextFieldLabel(
                        true,
                        stringResource(R.string.add_meet_detailed_meet_description_place_holder)
                    ) else null,
                    placeholder = TextFieldLabel(
                        false,
                        stringResource(R.string.add_meet_detailed_meet_description_place_holder)
                    ),
                    textStyle = typography.bodyMedium,
                    clear = { callback?.onDescriptionClear() }
                )
                Text(
                    "${state.description.length}/120",
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colorScheme.onTertiary, textAlign = TextAlign.End,
                    style = typography.headlineSmall
                )
            }
        }
    }
}

@Composable
fun Additionally(
    state: DetailedState,
    callback: DetailedCallback? = null
): FilterModel {
    return FilterModel(stringResource(R.string.add_meet_conditions_additionally)) {
        Row(Modifier.fillMaxWidth()) {
            DataTimeCard(state.date, DataTimeType.DATE, Modifier.weight(1f))
            { callback?.onDateClick() }
            Spacer(Modifier.width(16.dp))
            DataTimeCard(state.time, DataTimeType.TIME, Modifier.weight(1f))
            { callback?.onTimeClick() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetPlace(
    state: DetailedState,
    modifier: Modifier = Modifier,
    callback: DetailedCallback? = null
) {
    Column {
        Column(modifier.fillMaxWidth()) {
            Card(
                { callback?.onMeetPlaceClick() },
                Modifier.fillMaxWidth(), (true), shapes.medium,
                CardDefaults.cardColors(colorScheme.primaryContainer)
            ) {
                if (state.meetPlace != null)
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            state.meetPlace.first,
                            Modifier
                                .padding(top = 8.dp)
                                .padding(horizontal = 16.dp),
                            colorScheme.onTertiary, style = typography.headlineSmall
                        )
                        Text(
                            state.meetPlace.second,
                            Modifier
                                .padding(top = 2.dp, bottom = 10.dp)
                                .padding(horizontal = 16.dp),
                            colorScheme.tertiary,
                            style = typography.bodyMedium
                        )
                    }
                else Text(
                    stringResource(R.string.add_meet_detailed_meet_place), Modifier.padding(16.dp),
                    colorScheme.onTertiary, style = typography.bodyMedium
                )
            }
            Text(
                stringResource(R.string.add_meet_detailed_meet_place_place_holder),
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp),
                colorScheme.onTertiary, style = typography.headlineSmall
            )
        }
    }
}

@Composable
fun HideMeetPlace(
    state: DetailedState,
    modifier: Modifier = Modifier,
    callback: DetailedCallback? = null
) {
    CheckBoxCard(
        stringResource(R.string.add_meet_detailed_meet_hide_place),
        modifier.fillMaxWidth(), state.hideMeetPlace
    ) { callback?.onHideMeetPlaceClick() }
}