package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardCapitalization.Companion.Sentences
import androidx.compose.ui.text.input.KeyboardType.Companion.Text
import androidx.compose.ui.text.style.TextAlign.Companion.End
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Tags
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.*

@Composable
fun tags(
    state: DetailedState,
    callback: DetailedCallback? = null,
) = FilterModel(
    name = stringResource(R.string.add_meet_detailed_tags),
    details = stringResource(R.string.add_meet_detailed_tags_maximum, (3))
) {
    Column {
        Tags(
            tagList = state.tagList,
            onClick = { callback?.onTagsClick() },
            online = state.online
        ) { callback?.onTagDelete(it) }
    }
}

@Composable
fun description(
    state: DetailedState,
    callback: DetailedCallback? = null,
) = FilterModel(
    name = stringResource(R.string.add_meet_detailed_meet_description)
) {
    Column {
        GTextField(
            value = state.description, onValueChange = {
                if(it.length <= 120)
                    callback?.onDescriptionChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = descriptionColors(state.online),
            shape = shapes.medium,
            label = if(state.description.isNotEmpty())
                textFieldLabel(
                    label = true,
                    text = stringResource(R.string.add_meet_detailed_meet_description_place_holder),
                    labelFont = typography.headlineSmall
                        .copy(colorScheme.onTertiary),
                ) else null,
            placeholder = textFieldLabel(
                label = false,
                text = stringResource(R.string.add_meet_detailed_meet_description_place_holder),
                holderFont = typography.bodyMedium
            ),
            textStyle = typography.bodyMedium,
            keyboardOptions = Default.copy(
                imeAction = Done,
                keyboardType = Text,
                capitalization = Sentences
            ),
            clear = {
                callback?.onDescriptionClear()
            }
        )
        Text(
            text = "${state.description.length}/120",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, end = 16.dp),
            style = typography.headlineSmall.copy(
                color = colorScheme.onTertiary,
                textAlign = End,
            )
        )
    }
}

@Composable
fun additionally(
    state: DetailedState,
    callback: DetailedCallback? = null,
) = FilterModel(
    name = stringResource(R.string.add_meet_conditions_additionally)
) {
    Row(Modifier.fillMaxWidth()) {
        DataTimeCard(
            text = state.date,
            type = DataTimeType.DATE,
            online = state.online,
            modifier = Modifier.weight(1f)
        ) { callback?.onDateClick() }
        Spacer(Modifier.width(16.dp))
        DataTimeCard(
            text = state.time,
            type = DataTimeType.TIME,
            online = state.online,
            modifier = Modifier.weight(1f)
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
                onClick = {
                    callback?.onMeetPlaceClick()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = true,
                shape = shapes.medium,
                colors = cardColors(
                    colorScheme.primaryContainer
                )
            ) {
                state.meetPlace?.let {
                    Column(Modifier.fillMaxWidth()) {
                        state.meetPlace
                        Text(
                            text = it.first,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .padding(horizontal = 16.dp),
                            color = colorScheme.onTertiary,
                            style = typography.headlineSmall
                        )
                        Text(
                            text = it.second,
                            modifier = Modifier
                                .padding(
                                    top = 2.dp,
                                    bottom = 10.dp
                                )
                                .padding(
                                    horizontal = 16.dp
                                ),
                            color = colorScheme.tertiary,
                            style = typography.bodyMedium
                        )
                    }
                } ?: run {
                    Text(
                        text = stringResource(R.string.add_meet_detailed_meet_place),
                        modifier = Modifier.padding(16.dp),
                        color = colorScheme.onTertiary,
                        style = typography.bodyMedium,
                        fontWeight = SemiBold
                    )
                }
            }
        }
        Text(
            text = stringResource(
                R.string.add_meet_detailed_meet_place_place_holder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 32.dp),
            color = colorScheme.onTertiary,
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
        label = stringResource(
            R.string.add_meet_detailed_meet_hide_place
        ),
        modifier = modifier.fillMaxWidth(),
        state = state.hideMeetPlace,
        online = state.online
    ) { callback?.onHideMeetPlaceClick() }
}