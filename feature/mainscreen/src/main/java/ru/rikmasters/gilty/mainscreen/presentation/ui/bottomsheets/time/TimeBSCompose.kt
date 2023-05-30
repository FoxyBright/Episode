package ru.rikmasters.gilty.mainscreen.presentation.ui.bottomsheets.time

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.toSp
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
        TopBar(
            isNotEmpty = state.selectedTime
                .isNotBlank()
        ) { callback?.onClear() }
        GradientButton(
            Modifier
                .align(BottomCenter)
                .padding(bottom = 32.dp),
            stringResource(R.string.save_button),
        ) { callback?.onSave() }
    }
}

@Composable
private fun TopBar(
    isNotEmpty: Boolean,
    modifier: Modifier = Modifier,
    onClear: () -> Unit,
) {
    Row(
        modifier.fillMaxWidth(),
        SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.meeting_meet_time_label),
            color = colorScheme.tertiary,
            style = typography.labelLarge,
        )
        if(isNotEmpty) Text(
            text = stringResource(R.string.meeting_filter_clear),
            modifier = Modifier.clickable(
                MutableInteractionSource(), (null)
            ) { onClear() },
            style = typography.bodyMedium.copy(
                color = colorScheme.primary,
                fontSize = 16.dp.toSp(),
                fontWeight = SemiBold
            ),
        )
    }
}