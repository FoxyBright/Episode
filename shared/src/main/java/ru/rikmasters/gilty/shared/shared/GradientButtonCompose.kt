package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun GButtonPreview() {
    GiltyTheme {
        Column {
            GradientButton(
                text = stringResource(R.string.meeting_shared_button),
                icon = R.drawable.ic_shared,
                online = true
            ) {}
            GradientButton(
                Modifier.padding(top = 16.dp),
                text = stringResource(R.string.next_button),
                enabled = false
            ) {}
            GradientButton(
                Modifier.padding(top = 16.dp),
                text = stringResource(R.string.confirm_button),
                smallText = stringResource(
                    R.string.meeting_filter_meeting_find, (26)
                ),
            ) {}
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GradientButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    online: Boolean = false,
    shape: CornerBasedShape = shapes.extraLarge,
    smallText: String? = null,
    icon: Int? = null,
    onDisabledClick: () -> Unit = {},
    onClick: () -> Unit,
) {
    val color = when {
        enabled && online -> green()
        enabled && !online -> red()
        !enabled && online -> listOf(
            colorScheme.inverseSurface,
            colorScheme.inverseSurface
        )
        else -> listOf(
            colorScheme.inversePrimary,
            colorScheme.inversePrimary
        )
    }
    Box(
        modifier.background(
            color = colorScheme.background,
            shape = shape
        )
    ) {
        Card(
            onClick = if(enabled) onClick
            else onDisabledClick,
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            colors = cardColors(
                containerColor = Transparent,
                disabledContainerColor = Transparent,
            )
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(
                        horizontalGradient(color),
                        shape
                    )
                    .padding(
                        vertical = smallText
                            ?.let { 8.dp } ?: 16.dp
                    ), Center
            ) { ButtonText(icon, text, smallText) }
        }
    }
}

@Composable
private fun ButtonText(
    icon: Int?,
    text: String,
    smallText: String?,
) {
    Row(
        Modifier, Start,
        CenterVertically
    ) {
        icon?.let {
            Image(
                painterResource(it), (null),
                Modifier.padding(end = 6.dp)
            )
        }
        Column(
            Modifier, Top,
            CenterHorizontally
        ) {
            Text(
                text, style = typography
                    .bodyLarge.copy(White)
            )
            smallText?.let {
                Text(
                    it, style = typography
                        .headlineSmall.copy(White)
                )
            }
        }
    }
}