package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

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
    SquareCheckBox(checkBoxState, Modifier.padding(10.dp)) { checkBoxState = it }
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun LockerCheckBoxPreview() {
    var checkBoxState by remember { mutableStateOf(true) }
    LockerCheckBox((checkBoxState), Modifier.padding(10.dp)) { checkBoxState = it }
}

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun ObserveCheckBoxPreview() {
    var checkBoxState by remember { mutableStateOf(true) }
    ObserveCheckBox((checkBoxState), Modifier.padding(10.dp)) { checkBoxState = it }
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
    Image(
        painterResource(if (checked) changedImages.first() else changedImages.last()),
        null,
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
                if (checked) R.drawable.ic_grid_screen_button
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
    onCheckedChange: (Boolean) -> Unit
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
                if (checked) R.drawable.off_observe_checkbox
                else R.drawable.on_observe_checkbox
            ),
            null,
            Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LockerCheckBox(
    checked: Boolean = false,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit
) {
    IconButton({ onCheckedChange(!checked) }) {
        Icon(
            painterResource(if (checked) R.drawable.ic_lock_open else R.drawable.ic_lock_close),
            null,
            modifier.size(24.dp),
            ThemeExtra.colors.lockColors
        )
    }
}
