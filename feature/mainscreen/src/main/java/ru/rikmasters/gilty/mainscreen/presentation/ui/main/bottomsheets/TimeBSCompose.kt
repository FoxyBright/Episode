package ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

private const val START_TIME = "00"

@Composable
@SuppressLint(
    "UnusedTransitionTargetStateParameter",
    "UnrememberedMutableState"
)
fun TimeBS( // TODO Пока заглушка - переписать в нормальном виде
    modifier: Modifier = Modifier,
    onSave: ((String) -> Unit)? = null
) {
    var minutes by remember {
        mutableStateOf(START_TIME)
    }
    var hours by remember {
        mutableStateOf(START_TIME)
    }
    var time by remember {
        mutableStateOf("")
    }
    var resetTime by remember {
        mutableStateOf("")
    }
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
            if(time.isNotBlank()) Text(
                stringResource(R.string.meeting_filter_clear),
                Modifier.clickable(
                    MutableInteractionSource(), (null)
                ) {
                    minutes = START_TIME
                    hours = START_TIME
                    time = resetTime
                }, colorScheme.primary,
                style = typography.bodyMedium,
                fontWeight = Medium
            )
        }
        ScrollTimePicker(
            Modifier.fillMaxWidth(),
            minutes, hours
        ) { h, m -> time = "$h:$m" }
        GradientButton(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            stringResource(R.string.save_button), true
        ) {
            resetTime = time
            onSave?.let { it(time) }
        }
    }
}

@Preview
@Composable
private fun TimeBSPreview() {
    GiltyTheme {
        TimeBS()
    }
}