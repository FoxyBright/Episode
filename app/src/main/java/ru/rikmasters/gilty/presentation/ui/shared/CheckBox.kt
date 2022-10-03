package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R

@Composable
fun CheckBox(modifier: Modifier, checkboxState: Boolean) {
    var check by remember { mutableStateOf(checkboxState) }
    var imageResource by remember { mutableStateOf(0) }
    val click: () -> Unit
    if (check) {
        imageResource = R.drawable.enabled_check_box
        click = { check = false }
    } else {
        imageResource = R.drawable.disabled_check_box
        click = { check = true }
    }
    Image(
        painterResource(imageResource), "check image",
        modifier
            .size(24.dp)
            .clickable { click() }
    )
}