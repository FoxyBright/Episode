package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val TIMES_START = "00"
private const val MINUTES_END = "59"
private const val HOURS_END = "23"

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ScrollTimePickerPreview() {
    val time = remember { mutableStateOf("00:00") }
    ScrollTimePicker(
        Modifier.width(200.dp),
        mutableStateOf("00"),
        mutableStateOf("00")
    ) { h, m -> time.value = "$h:$m" }
    Text(
        time.value,
        Modifier
            .width(200.dp)
            .padding(10.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun ScrollTimePicker(
    modifier: Modifier = Modifier,
    minutes: MutableState<String>,
    hours: MutableState<String>,
    onValueChange: (hour: String, minute: String) -> Unit
) {
    var hoursValue by remember { minutes }
    var minutesValue by remember { hours }
    Row(
        modifier,
        Arrangement.Center,
        Alignment.CenterVertically
    ) {
        ListItemPicker(
            timeReplacer(hoursValue, HOURS_END),
            getTimesList(0..HOURS_END.toInt()),
            Modifier.padding(end = 5.dp),
            { timeReplacer(it, HOURS_END) },
            MaterialTheme.colorScheme.outline
        ) { hoursValue = it }
        ListItemPicker(
            timeReplacer(minutesValue, MINUTES_END),
            getTimesList(0..MINUTES_END.toInt()),
            Modifier.padding(start = 5.dp),
            { timeReplacer(it, MINUTES_END) },
            MaterialTheme.colorScheme.outlineVariant
        ) { minutesValue = it }
    }
    onValueChange(
        timeReplacer(hoursValue, "23"),
        timeReplacer(minutesValue, "59")
    )
}

private fun timeReplacer(it: String, end: String): String {
    return when (it) {
        "start" -> end
        "end" -> TIMES_START
        else -> it
    }
}

private fun getTimesList(range: Iterable<Int>): List<String> {
    val list = arrayListOf<String>()
    list.add("start")
    range.forEach {
        if (it.toString().length != 1) list.add(it.toString())
        else list.add("0${it}")
    }
    list.add("end")
    return list
}