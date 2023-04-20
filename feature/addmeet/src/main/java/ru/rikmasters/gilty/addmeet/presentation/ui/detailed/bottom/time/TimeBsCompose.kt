package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.ListItemPicker
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(showBackground = true)
@Composable
private fun DateTimeBSPreview() {
    GiltyTheme {
        DateTimeBS(
            DateTimeBSState(
                TODAY_LABEL, TIME_START,
                TIME_START, (false), (true)
            ), Modifier.padding(16.dp)
        )
    }
}

data class DateTimeBSState(
    val date: String,
    val hour: String,
    val minute: String,
    val online: Boolean,
    val isActive: Boolean,
)

interface DateTimeBSCallback {
    
    fun dateChange(it: String) {}
    fun hourChange(it: String) {}
    fun minuteChange(it: String) {}
    fun onSave() {}
}

@Composable
fun DateTimeBS(
    state: DateTimeBSState,
    modifier: Modifier = Modifier,
    callback: DateTimeBSCallback? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            stringResource(R.string.add_meet_detailed_meet_date_episode),
            Modifier.padding(bottom = 16.dp),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        DateTimePickerContent(
            Modifier.fillMaxWidth(),
            state, callback
        )
        GradientButton(
            Modifier.padding(vertical = 28.dp),
            stringResource(R.string.save_button),
            state.isActive, state.online
        ) { callback?.onSave() }
    }
}

@Composable
private fun DateTimePickerContent(
    modifier: Modifier = Modifier,
    state: DateTimeBSState,
    callback: DateTimeBSCallback? = null,
) {
    Box(
        modifier.background(
            colorScheme.background
        ), Center
    ) {
        Row {
            ListItemPicker(
                state.date.ifBlank {
                    "$LOCAL_DATE$ZERO_TIME"
                }, getDate(), Modifier, {
                    it.format("E dd MMM")
                        .replaceFirstChar { c ->
                            c.uppercase()
                        }
                }
            ) { callback?.dateChange(it) }
            getTime().let { time ->
                ListItemPicker(
                    state.hour.ifBlank {
                        time.first.last()
                    }, time.first
                ) { callback?.hourChange(it) }
                ListItemPicker(
                    state.minute.ifBlank {
                        time.second.last()
                    }, time.second
                ) { callback?.minuteChange(it) }
            }
        }
    }
}