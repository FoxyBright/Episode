package ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
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
                TimeBsState(("10"), ("12"), ("10:12"))
            )
        }
    }
}

data class TimeBsState(
    val minutes: String,
    val hours: String,
    val time: String,
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
    Column(
        modifier
            .height(350.dp)
            .padding(16.dp)
            .padding(top = 12.dp),
        SpaceBetween
    ) {
        Row(
            Modifier.fillMaxWidth(),
            SpaceBetween, CenterVertically
        ) {
            Text(
                stringResource(R.string.meeting_meet_time_label),
                Modifier, colorScheme.tertiary,
                style = typography.labelLarge,
            )
            if(state.time.isNotBlank()) Text(
                stringResource(R.string.meeting_filter_clear),
                Modifier.clickable(
                    MutableInteractionSource(), (null)
                ) { callback?.onClear() },
                colorScheme.primary,
                style = typography.bodyMedium,
                fontWeight = Medium
            )
        }
        ScrollTimePicker(
            Modifier.fillMaxWidth(),
            state.minutes, state.hours,
            { callback?.onHourChange(it) }
        ) { callback?.onMinuteChange(it) }
        GradientButton(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            stringResource(R.string.save_button), (true)
        ) { callback?.onSave() }
    }
}