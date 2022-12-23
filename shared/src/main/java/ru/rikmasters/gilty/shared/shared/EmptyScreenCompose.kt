package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun EmptyScreen(
    text: String, icon: Int,
    modifier: Modifier = Modifier,
    imageSize: Dp? = null,
    betweenDistance: Dp = 26.dp
) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(
            modifier.fillMaxWidth(),
            Top, CenterHorizontally
        ) {
            Icon(
                painterResource(icon), (null),
                if(imageSize != null)
                    Modifier.size(imageSize)
                else Modifier, colorScheme.onTertiary
            )
            Text(
                text, Modifier.padding(betweenDistance),
                colorScheme.onTertiary,
                style = typography.bodyMedium,
                fontWeight = SemiBold,
                textAlign = Center
            )
        }
    }
}