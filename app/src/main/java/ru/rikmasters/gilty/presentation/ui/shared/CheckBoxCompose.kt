package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(showBackground = true)
@Composable
private fun CheckBoxPreviewEnabled() {
    var checkBoxState by remember { mutableStateOf(true) }
    CheckBox(checkBoxState, Modifier.padding(10.dp)) { checkBoxState = it }
}

@Preview(showBackground = true)
@Composable
private fun SquareCheckBoxPreview() {
    var checkBoxState by remember { mutableStateOf(true) }
    SquareCheckBox(checkBoxState, Modifier.padding(10.dp)) { checkBoxState = it }
}

@Preview(showBackground = true)
@Composable
private fun LockerCheckBoxPreview() {
    var checkBoxState by remember { mutableStateOf(true) }
    SquareCheckBox(checkBoxState, Modifier.padding(10.dp)) { checkBoxState = it }
}

@Composable
fun CheckBox(
    checked: Boolean = false,
    modifier: Modifier = Modifier,
    changedImages: List<Int> =
        listOf(
            R.drawable.enabled_check_box,
            R.drawable.disabled_check_box
        ),
    onCheckedChange: (Boolean) -> Unit
) {
    val imageResource: Int = if (checked) {
        changedImages.first()
    } else {
        changedImages.last()
    }
    Image(
        painterResource(imageResource), null,
        modifier
            .size(24.dp)
            .clip(CircleShape)
            .clickable { onCheckedChange(!checked) }
    )
}

@Composable
fun SquareCheckBox(
    checked: Boolean = false,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit
) {
    val imageResource: Int = if (checked) {
        R.drawable.ic_grid_screen_button
    } else {
        R.drawable.ic_swipe_screen_button
    }
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
            painterResource(imageResource), null,
        )
    }
}

@Composable
fun LockerCheckBox(
    checked: Boolean = false,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit
) {
    val imageResource: Int = if (checked) {
        R.drawable.ic_lock_open
    } else {
        R.drawable.ic_lock_close
    }
    IconButton({ onCheckedChange(!checked) }) {
        Icon(
            painterResource(imageResource),
            null,
            modifier.size(24.dp),
            ThemeExtra.colors.lockColors
        )
    }
}
