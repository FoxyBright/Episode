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
private const val MINUTES_END = "59"
private const val HOURS_END = "23"

@Preview
@Composable
private fun ScrollTimePickerPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
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
                timeReplacer(hours, HOURS_END),
                getTimesList(0..HOURS_END.toInt()),
                Modifier.weight(1f),
                { timeReplacer(it, HOURS_END) },
                colorScheme.outline, style
            ) { onHourChange(it) }
            Spacer(Modifier.width(16.dp))
            ListItemPicker(
                timeReplacer(minutes, MINUTES_END),
                getTimesList(0..MINUTES_END.toInt()),
                Modifier.weight(1f),
                { timeReplacer(it, MINUTES_END) },
                colorScheme.outline, style
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

private fun getTimesList(range: Iterable<Int>): List<String> {
    val list = arrayListOf<String>()
    list.add("start")
    range.forEach {
        if(it.toString().length != 1) list.add(it.toString())
        else list.add("0${it}")
    }
    list.add("end")
    return list
}