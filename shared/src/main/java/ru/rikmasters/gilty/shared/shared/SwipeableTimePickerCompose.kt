package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

private const val TIMES_START = "00"
private const val MINUTES_END = "55"
private const val HOURS_END = "23"

@Preview
@Composable
private fun ScrollTimePickerPreview() {
    GiltyTheme {
        Box(
            Modifier.background(colorScheme.background)
        ) {
            ScrollTimePicker(
                Modifier.width(200.dp),
                TIMES_START, TIMES_START, {}
            ) {}
        }
    }
}

@Composable
fun ScrollTimePicker(
    modifier: Modifier = Modifier,
    minutes: String,
    hours: String,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit,
) {
    Box(
        modifier.background(
            colorScheme.background
        ), Alignment.Center
    ) {
        Row(Modifier.fillMaxWidth(0.5f)) {
            val style = typography.labelLarge.copy(
                colorScheme.tertiary,
                textAlign = Center,
                fontSize = 25.sp
            )
            ListItemPicker(
                label = { timeReplacer(it, HOURS_END) },
                value = timeReplacer(hours, HOURS_END),
                dividersColor = colorScheme.outline,
                modifier = Modifier.weight(1f),
                list = timesList.first,
                textStyle = style
            ) { onHourChange(it) }
            Spacer(Modifier.width(16.dp))
            ListItemPicker(
                value = timeReplacer(minutes, MINUTES_END),
                label = { timeReplacer(it, MINUTES_END) },
                dividersColor = colorScheme.outline,
                modifier = Modifier.weight(1f),
                list = timesList.second,
                textStyle = style
            ) { onMinuteChange(it) }
        }
    }
}

private fun timeReplacer(it: String, end: String): String {
    return when(it) {
        "start" -> end
        "end" -> TIMES_START
        else -> it
    }
}

private val timesList by lazy {
    val (hours, minutes) = 0..23 to 0..55
    (ArrayList<String>(25) to ArrayList<String>(14))
        .let { (listHours, listMin) ->
            listHours.add("start")
            listMin.add("start")
            minutes.forEach {
                if(it in hours) listHours
                    .add("%02d".format(it))
                if(it % 5 == 0) listMin
                    .add("%02d".format(it))
            }
            listHours.add("end")
            listMin.add("end")
            listHours to listMin
        }
}