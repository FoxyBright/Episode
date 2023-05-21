package ru.rikmasters.gilty.translation.presentation.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.ListItemPicker

@Composable
fun ExtendBottomSheet(
    value: String,
    onValueChange: ((String) -> Unit)? = null,
    onSave: (() -> Unit)? = null,
) {
    Box(
    Modifier
    .fillMaxWidth()
    .fillMaxHeight(0.55f)
    .padding(16.dp)
    .padding(top = 10.dp)
    ) {
        DurationPicker(
            value, Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .align(Alignment.TopCenter)
        ) { onValueChange?.let { c -> c(it) } }
        Text(
            stringResource(R.string.add_meet_detailed_meet_duration),
            Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.TopStart),
            MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.labelLarge
        )
        GradientButton(
            Modifier
                .padding(vertical = 28.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.save_button),
            online = true
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
            MaterialTheme.colorScheme.background
        ), Alignment.Center
    ) {
        Box(Modifier.padding(horizontal = 20.dp)) {
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