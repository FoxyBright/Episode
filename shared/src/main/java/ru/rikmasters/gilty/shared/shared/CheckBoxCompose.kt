package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun CheckBoxPreviewEnabled() {
    var checkBoxState by remember { mutableStateOf(true) }
    CheckBox(checkBoxState, Modifier.padding(10.dp)) { checkBoxState = it }
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun SquareCheckBoxPreview() {
    var checkBoxState by remember { mutableStateOf(true) }
    SquareCheckBox(
        checkBoxState,
        Modifier.padding(10.dp)
    ) { checkBoxState = it }
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ObserveCheckBoxPreview() {
    var checkBoxState by remember { mutableStateOf(true) }
    ObserveCheckBox(
        (checkBoxState),
        Modifier.padding(10.dp)
    ) { checkBoxState = it }
}

@Composable
fun CheckBox(
    checked: Boolean = false,
    modifier: Modifier = Modifier,
    changedImages: List<Int> =
        listOf(
            R.drawable.enabled_check_box,
            if(isSystemInDarkTheme())
                R.drawable.disabled_check_box_dark
            else R.drawable.disabled_check_box
        ),
    color: Color? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    Image(
        painterResource(
            if(checked) changedImages.first()
            else changedImages.last()
        ), (null), modifier
            .size(24.dp)
            .clickable { onCheckedChange?.let { it(!checked) } },
        colorFilter = color?.let { ColorFilter.tint(it) }
    )
}

@Composable
fun SquareCheckBox(
    checked: Boolean = false,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) {
    Box(
        modifier
            .size(56.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onCheckedChange(!checked) }
            .padding(10.dp),
        Alignment.Center
    ) {
        Image(
            painterResource(
                if(checked) R.drawable.ic_grid_screen_button
                else R.drawable.ic_swipe_screen_button
            ),
            null,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ObserveCheckBox(
    checked: Boolean = false,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(
        { onCheckedChange(!checked) },
        modifier
            .width(136.dp)
            .height(26.dp),
        colors = CardDefaults.cardColors(Color.Transparent)
    ) {
        Image(
            painterResource(
                if(checked) R.drawable.off_observe_checkbox
                else R.drawable.on_observe_checkbox
            ),
            null,
            Modifier.fillMaxSize()
        )
    }
}