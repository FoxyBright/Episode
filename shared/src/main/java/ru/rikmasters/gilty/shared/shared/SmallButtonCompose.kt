package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
fun SmallButtonPreview() {
    GiltyTheme() {
        SmallButton(
            stringResource(R.string.notification_respond_accept_button),
            colorScheme.primary, White,
            Modifier.padding(6.dp)
        ) {}
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SmallButton(
    text: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        onClick, modifier, enabled,
        shapes.large, cardColors(color)
    ) {
        Text(
            text, Modifier.padding(12.dp, 8.dp),
            textColor, style = typography.bodyMedium,
            fontWeight = SemiBold
        )
    }
}