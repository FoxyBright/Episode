package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun GradientButtonPreview() {
    GiltyTheme {
        GradientButton(text = "Далее", smallText = "Подробности") {}
    }
}

@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    gradientColors: List<Color> = listOf(
        ThemeExtra.colors.gradientColor1,
        ThemeExtra.colors.gradientColor2
    ),
    shape: CornerBasedShape = MaterialTheme.shapes.extraLarge,
    smallText: String? = null,
    disabledColors: List<Color> = listOf(ThemeExtra.colors.notActive, ThemeExtra.colors.notActive),
    onClick: () -> Unit,
) {
    Button(
        onClick,
        modifier,
        enabled,
        shape,
        ButtonDefaults.buttonColors(Color.Transparent),
        contentPadding = PaddingValues(),
    ) {
        Box(
            Modifier
                .background(
                    Brush.linearGradient(if (enabled) gradientColors else disabledColors),
                    shape
                )
                .fillMaxWidth()
                .padding(16.dp),
            Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text, style = ThemeExtra.typography.button)
                smallText?.let { Text(smallText, style = ThemeExtra.typography.ButtonLabelText) }
            }
        }
    }
}
