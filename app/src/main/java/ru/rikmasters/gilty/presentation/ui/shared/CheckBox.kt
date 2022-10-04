package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun CheckBoxPreview(){
    Row{
        CheckBox(true, Modifier.padding(10.dp))
        CheckBox(false, Modifier.padding(10.dp))
    }
}

@Composable
fun CheckBox(
    checked: Boolean = false,
    modifier: Modifier = Modifier,
    onCheckedChange: (() -> Unit)? = null
) {
    var check by remember { mutableStateOf(checked) }
    var imageResource by remember { mutableStateOf(0) }
    val click: () -> Unit
    if (check) {
        imageResource = R.drawable.enabled_check_box
        click = {
            check = !check
        }
    } else {
        imageResource = R.drawable.disabled_check_box
        click = {
            check = !check
        }
    }
    Image(
        painterResource(imageResource), "check image",
        modifier
            .size(24.dp)
            .clip(CircleShape)
            .clickable { click() }
    )
}