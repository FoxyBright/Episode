package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.duration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopStart
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
    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(16.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            stringResource(R.string.add_meet_detailed_meet_date_episode),
            Modifier
                .padding(bottom = 16.dp)
                .align(TopStart),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        DurationPicker(
            value, Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .align(Center)
        ) { onValueChange?.let { c -> c(it) } }
        GradientButton(
            Modifier
                .padding(vertical = 28.dp)
                .align(BottomCenter),
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
        modifier.background(
            colorScheme.background
        ), Center
    ) {
        Box(modifier.padding(horizontal = 20.dp)) {
            getDuration().let { list ->
                ListItemPicker(
                    value.ifBlank {
                        list[list.lastIndex - 2]
                    }, list,
                    doublePlaceHolders = true
                ) { onValueChange?.let { c -> c(it) } }
            }
        }
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