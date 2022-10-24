package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun GradientButtonPreview() {
    GiltyTheme {
        GradientButton({ }, text = "Далее", smallText = "Подробности", icon = R.drawable.ic_shared)
    }
}

@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    gradientColors: List<Color> = listOf(
        ThemeExtra.colors.gradientColor1,
        ThemeExtra.colors.gradientColor2
    ),
    shape: CornerBasedShape = MaterialTheme.shapes.extraLarge,
    smallText: String? = null,
    icon: Int? = null,
    disabledColors: List<Color> = listOf(
        ThemeExtra.colors.notActive,
        ThemeExtra.colors.notActive
    ),
) {
    Button(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = shape,
        enabled = enabled,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (enabled) gradientColors
                        else disabledColors
                    ),
                    shape = shape
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.let {
                    Image(
                        painterResource(it),
                        null,
                        Modifier.padding(end = 6.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = text,
                        style = ThemeExtra.typography.button
                    )
                    smallText?.let {
                        Text(it, style = ThemeExtra.typography.ButtonLabelText)
                    }
                }
            }
        }
    }
}
