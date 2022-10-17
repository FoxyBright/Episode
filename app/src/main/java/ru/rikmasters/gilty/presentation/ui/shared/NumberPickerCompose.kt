package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra


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
    dividerColor: Color = ThemeExtra.colors.grayIcon,
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