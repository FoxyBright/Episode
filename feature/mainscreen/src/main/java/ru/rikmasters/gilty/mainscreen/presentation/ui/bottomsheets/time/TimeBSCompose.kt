package ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.ScrollTimePicker
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun TimeBsPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            TimeBsContent(
                TimeBsState(
                    ("10"), ("12"), ("10:12"),
                    "00:00"
                )
            )
        }
    }
}

data class TimeBsState(
    val minutes: String,
    val hours: String,
    val time: String,
    val selectedTime: String,
)

interface TimeBSCallback {
    
    fun onSave()
    fun onHourChange(hour: String)
    fun onMinuteChange(minute: String)
    fun onClear()
}

@Composable
fun TimeBsContent(
    state: TimeBsState,
    modifier: Modifier = Modifier,
    callback: TimeBSCallback? = null,
) {
    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.55f)
            .padding(16.dp)
            .padding(top = 10.dp)
    ) {
        ScrollTimePicker(
            Modifier
                .fillMaxHeight(0.8f)
                .align(Alignment.TopCenter),
            state.minutes, state.hours,
            { callback?.onHourChange(it) }
        ) { callback?.onMinuteChange(it) }
        Text(
            stringResource(R.string.meeting_meet_time_label),
            Modifier, colorScheme.tertiary,
            style = typography.labelLarge,
        )
        GradientButton(
            Modifier
                .align(BottomCenter)
                .padding(bottom = 32.dp),
            stringResource(R.string.save_button),
        ) { callback?.onSave() }
    }
}