package ru.rikmasters.gilty.shared.shared

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
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun GradientButtonPreview() {
    GiltyTheme {
        GradientButton(
            text = "Далее",
            smallText = "Подробности",
            icon = R.drawable.ic_shared
        ) {}
    }
}

@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    online: Boolean = false,
    shape: CornerBasedShape = MaterialTheme.shapes.extraLarge,
    smallText: String? = null,
    icon: Int? = null,
    onClick: () -> Unit
) {
    Button(
        onClick, modifier.fillMaxWidth(), enabled, shape,
        ButtonDefaults.buttonColors(Color.Transparent),
        contentPadding = PaddingValues(),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    if (online) Brush.linearGradient(
                        if (enabled) Gradients.green() else listOf(
                            MaterialTheme.colorScheme.inverseSurface,
                            MaterialTheme.colorScheme.inverseSurface
                        )
                    )
                    else
                        Brush.linearGradient(
                            if (enabled) Gradients.red() else listOf(
                                MaterialTheme.colorScheme.inversePrimary,
                                MaterialTheme.colorScheme.inversePrimary
                            )
                        ), shape
                )
                .padding(16.dp, 16.dp), Alignment.Center
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
                    Text(text, color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    smallText?.let { Text(it, style = MaterialTheme.typography.headlineSmall) }
                }
            }
        }
    }
}
