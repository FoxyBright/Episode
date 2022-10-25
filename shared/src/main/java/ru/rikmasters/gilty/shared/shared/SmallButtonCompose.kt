package ru.rikmasters.gilty.shared.shared

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun SmallButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick, modifier,
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(color)
    ) {
        Text(
            text,
            color = ThemeExtra.colors.mainTextColor,
            style = ThemeExtra.typography.Body1Sb
        )
    }
}