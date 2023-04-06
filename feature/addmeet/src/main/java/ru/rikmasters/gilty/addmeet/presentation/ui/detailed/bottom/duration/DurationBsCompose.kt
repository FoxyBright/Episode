package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.duration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
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
            colorScheme.tertiary,
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
    val list = getDuration()
    Box(
        modifier.background(colorScheme.background),
        Center
    ) {
        ListItemPicker(value.ifBlank { list.last() }, list)
        { onValueChange?.let { c -> c(it) } }
    }
}

fun getDuration() = arrayListOf<String>().let { list ->
    var hour = 0
    var min = 20
    repeat(33) {
        val hLabel = "$hour ${if(hour == 1) "час" else "часа"}"
        val mLabel = if(min > 0) "$min минут" else ""
        list.add("${if(hour > 0) "$hLabel " else ""}$mLabel")
        min += 5
        if(min == 60) {
            min = 0; ++hour
        }
    }; list.reverse()
    list
}