package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.Gradients.red
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
    shape: CornerBasedShape = shapes.extraLarge,
    smallText: String? = null,
    icon: Int? = null,
    onClick: () -> Unit
) {
    val inverse = listOf(
        colorScheme.inversePrimary,
        colorScheme.inversePrimary
    )
    Button(
        onClick, modifier.fillMaxWidth(),
        enabled, shape,
        buttonColors(
            containerColor = Transparent,
            disabledContainerColor = Transparent
        ), contentPadding = PaddingValues(),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    if(online) linearGradient(
                        if(enabled) green()
                        else inverse
                    )
                    else linearGradient(
                        if(enabled) red()
                        else inverse
                    ), shape
                )
                .padding(16.dp, 16.dp), Center
        ) {
            Row(Modifier, Start, CenterVertically) {
                icon?.let {
                    Image(
                        painterResource(it),
                        null,
                        Modifier.padding(end = 6.dp)
                    )
                }
                Column(Modifier, Top, CenterHorizontally) {
                    Text(text, color = White, style = typography.bodyLarge)
                    smallText?.let { Text(it, style = typography.headlineSmall) }
                }
            }
        }
    }
}
