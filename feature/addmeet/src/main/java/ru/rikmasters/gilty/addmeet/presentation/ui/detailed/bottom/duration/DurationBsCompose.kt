package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.duration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.HOURS_IN_DAY
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.ListItemPicker
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(showBackground = true)
@Composable
private fun DurationBottomSheetPreview() {
    GiltyTheme {
        DurationBottomSheet(
            "", Modifier.padding(16.dp),
            false
        )
    }
}

@Composable
fun DurationBottomSheet(
    value: String,
    modifier: Modifier = Modifier,
    online: Boolean,
    onValueChange: ((String) -> Unit)? = null,
    onSave: (() -> Unit)? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.add_meet_meet_duration_label),
            Modifier.padding(bottom = 16.dp, top = 10.dp),
            MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.labelLarge
        )
        DurationPicker(value, Modifier.fillMaxWidth())
        { value -> onValueChange?.let { it(value) } }
        GradientButton(
            Modifier.padding(vertical = 28.dp),
            stringResource(R.string.save_button),
            online = online
        ) { onSave?.let { it() } }
    }
}

@Composable
private fun DurationPicker(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: ((String) -> Unit)? = null,
) {
    Box(
        modifier.background(MaterialTheme.colorScheme.background),
        Alignment.Center
    ) {
        ListItemPicker(value, getDuration())
        { onValueChange?.let { c -> c(it) } }
    }
}

private fun getDuration(): List<String> {
    val list = arrayListOf<String>()
    repeat(HOURS_IN_DAY) { hour ->
        repeat(4) { minute ->
            list.add(
                "${
                    if(hour != 0) "$hour ${
                        when("$hour".last()) {
                            '1' -> "час"
                            '2', '3', '4' -> "часа"
                            else -> "часов"
                        }
                    }" else ""
                } ${if(minute != 0) "${(minute) * 15} минут" else ""}"
            )
        }
    }; list.add("Сутки"); return list.minus(list[0])
}