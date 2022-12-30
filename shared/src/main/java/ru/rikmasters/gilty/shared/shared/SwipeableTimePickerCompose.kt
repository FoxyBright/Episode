package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                TIMES_START, TIMES_START
            )
        }
    }
}

@Composable
fun ScrollTimePicker(
    modifier: Modifier = Modifier,
    minutes: String,
    hours: String,
    onValueChange: ((hour: String, minute: String) -> Unit)? = null
) {
    var hoursValue by remember {
        mutableStateOf(minutes)
    }
    var minutesValue by remember {
        mutableStateOf(hours)
    }
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
            colorScheme.outline
        ) { hoursValue = it }
        ListItemPicker(
            timeReplacer(minutesValue, MINUTES_END),
            getTimesList(0..MINUTES_END.toInt()),
            Modifier.padding(start = 5.dp),
            { timeReplacer(it, MINUTES_END) },
            colorScheme.outline
        ) { minutesValue = it }
    }
    onValueChange?.let {
        it(
            timeReplacer(hoursValue, "23"),
            timeReplacer(minutesValue, "59")
        )
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