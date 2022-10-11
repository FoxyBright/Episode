package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R

@Preview(showBackground = true)
@Composable
private fun CheckBoxPreviewEnabled() {
    var checkBoxState by remember { mutableStateOf(true) }
    CheckBox(checkBoxState, Modifier.padding(10.dp)) {
        checkBoxState = it
    }
}

@Preview(showBackground = true)
@Composable
private fun CheckBoxPreview() {
    var checkBoxState by remember { mutableStateOf(false) }
    CheckBox(checkBoxState, Modifier.padding(10.dp)) {
        checkBoxState = it
    }
}

@Composable
fun CheckBox(
    checked: Boolean = false,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit
) {
    val imageResource: Int = if (checked) {
        R.drawable.enabled_check_box
    } else {
        R.drawable.disabled_check_box
    }
    Image(
        painterResource(imageResource), stringResource(R.string.check_image),
        modifier
            .size(24.dp)
            .clip(CircleShape)
            .clickable { onCheckedChange(!checked) }
    )
}