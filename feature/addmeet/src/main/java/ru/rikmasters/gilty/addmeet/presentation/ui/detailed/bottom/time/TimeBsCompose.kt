package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.ListItemPicker
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

private const val MINUTES_STEP = 5
private const val HOURS_STEP = 1

@Preview(showBackground = true)
@Composable
private fun DateTimeBSPreview() {
    GiltyTheme {
        DateTimeBS(
            DateTimeBSState(
                TODAY_LABEL, TIME_START,
                TIME_START, (false), (true)
            ), Modifier.padding(16.dp)
        )
    }
}

data class DateTimeBSState(
    val date: String,
    val hour: String,
    val minute: String,
    val online: Boolean,
    val isActive: Boolean,
)

interface DateTimeBSCallback {
    
    fun dateChange(it: String) {}
    fun hourChange(it: String) {}
    fun minuteChange(it: String) {}
    fun onSave() {}
}

@Composable
fun DateTimeBS(
    state: DateTimeBSState,
    modifier: Modifier = Modifier,
    callback: DateTimeBSCallback? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            stringResource(R.string.add_meet_detailed_meet_date_episode),
            Modifier.padding(bottom = 16.dp),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        DateTimePickerContent(
            Modifier.fillMaxWidth(),
            state, callback
        )
        GradientButton(
            Modifier.padding(vertical = 28.dp),
            stringResource(R.string.save_button),
            state.isActive, state.online
        ) { callback?.onSave() }
    }
}

@Composable
private fun DateTimePickerContent(
    modifier: Modifier = Modifier,
    state: DateTimeBSState,
    callback: DateTimeBSCallback? = null,
) {
    Box(
        modifier.background(colorScheme.background),
        Alignment.Center
    ) {
        Row {
            ListItemPicker(state.date, getDate())
            { callback?.dateChange(it) };ListItemPicker(
            replacer(state.hour, "$HOURS_IN_DAY"),
            getTime(0..HOURS_IN_DAY, HOURS_STEP),
            Modifier, { replacer(it, "$HOURS_IN_DAY") }
        ) { callback?.hourChange(it) };ListItemPicker(
            replacer(state.minute, "$MINUTES_IN_HOUR"),
            getTime(0..MINUTES_IN_HOUR, MINUTES_STEP),
            Modifier, { replacer(it, "$MINUTES_IN_HOUR") }
        ) { callback?.minuteChange(it) }
        }
    }
}