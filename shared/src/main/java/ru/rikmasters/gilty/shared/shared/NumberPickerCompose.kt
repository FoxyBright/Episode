package ru.rikmasters.gilty.shared.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun NumberPickerPreview() {
    GiltyTheme {
        var pickerValue by remember { mutableStateOf(18) }
        NumberPicker(
            value = pickerValue,
            onValueChange = {
                pickerValue = it
            },
            range = 14..60
        )
    }
}

@Composable
fun NumberPicker(
    modifier: Modifier = Modifier,
    label: (Int) -> String = {
        it.toString()
    },
    value: Int,
    onValueChange: (Int) -> Unit,
    dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
    range: Iterable<Int>
) {
    ListItemPicker(
        modifier = modifier,
        label = label,
        value = value,
        onValueChange = onValueChange,
        dividersColor = dividerColor,
        list = range.toList()
    )
}