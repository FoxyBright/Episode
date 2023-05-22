package ru.rikmasters.gilty.translation.presentation.ui.content.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.ListItemPicker
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun ExtendBottomSheet(
    onSave: ((Int) -> Unit),
) {
    var current by remember { mutableStateOf("") }
    DurationBottomSheet(
        value = current,
        modifier = Modifier,
        online = true,
        { current = it },
    ) {
        if (current.contains("1")) {
            val minutes = current.substringAfterLast('с').substringBeforeLast('м').trim().toInt()
            onSave(minutes + 60)
        } else {
            val minutes = current.substringBeforeLast('м').trim().toInt()
            onSave(minutes)
        }
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
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.55f)
            .background(color = Color.Black)
    ) {
        DurationPicker(
            value, Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .align(Alignment.Center)
        ) { onValueChange?.let { c -> c(it) } }
        Column(
            modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .height(5.dp)
                    .width(40.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(11.dp),
                color = ThemeExtra.colors.bottomSheetGray
            ) {}
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                stringResource(R.string.translations_expired_append),
                Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.Start),
                ThemeExtra.colors.white,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            GradientButton(
                Modifier
                    .padding(vertical = 28.dp)
                    .align(Alignment.CenterHorizontally),
                stringResource(R.string.translations_expired_button_continue),
                online = online
            ) { onSave?.let { it() } }
            Spacer(modifier = Modifier.height(16.dp))
        }
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
            Color.Black
        ), Alignment.Center
    ) {
        Box(Modifier.padding(horizontal = 20.dp)) {
            getDuration().let { list ->
                ListItemPicker(
                    value.ifBlank {
                        list[list.lastIndex - 2]
                    }, list,
                    doublePlaceHolders = true,
                    dividersColor = Color(0xFF767373),
                    textStyle = MaterialTheme.typography.labelLarge.copy(
                        ThemeExtra.colors.white, textAlign = TextAlign.Center
                    )
                ) { onValueChange?.let { c -> c(it) } }
            }
        }
    }
}

fun getDuration() = arrayListOf<String>().let { list ->
    var hour = 0
    var min = 30
    repeat(5) {
        val hLabel = "$hour ${if(hour == 1) "час" else "часа"}"
        val mLabel = if(min > 0) "$min минут" else ""
        list.add("${if(hour > 0) "$hLabel " else ""}$mLabel")
        min += 15
        if(min == 60) {
            min = 0; ++hour
        }
    }; list.reverse()
    list
}
