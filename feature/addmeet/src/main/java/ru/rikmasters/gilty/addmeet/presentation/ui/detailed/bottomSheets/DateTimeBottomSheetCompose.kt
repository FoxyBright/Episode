package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import ru.rikmasters.gilty.shared.common.extentions.HOURS_IN_DAY
import ru.rikmasters.gilty.shared.common.extentions.MINUTES_IN_HOUR
import ru.rikmasters.gilty.shared.common.extentions.TIME_START
import ru.rikmasters.gilty.shared.common.extentions.TODAY_LABEL
import ru.rikmasters.gilty.shared.common.extentions.getDate
import ru.rikmasters.gilty.shared.common.extentions.getTime
import ru.rikmasters.gilty.shared.common.extentions.replacer
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
                TIME_START, false
            ),
            Modifier.padding(16.dp)
        )
    }
}

data class DateTimeBSState(
    val date: String,
    val hour: String,
    val minute: String,
    val online: Boolean
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
    callback: DateTimeBSCallback? = null
) {
    Column(modifier.fillMaxWidth()) {
        Text(
            "Дата и время встречи",
            Modifier.padding(bottom = 16.dp),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        DateTimePickerContent(state, callback, Modifier.fillMaxWidth())
        GradientButton(
            Modifier.padding(vertical = 28.dp),
            stringResource(R.string.save_button),
            online = state.online
        ) { callback?.onSave() }
    }
}

@Composable
private fun DateTimePickerContent(
    state: DateTimeBSState,
    callback: DateTimeBSCallback? = null,
    modifier: Modifier = Modifier
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