package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CardRow(
    label: String, text: String,
    modifier: Modifier = Modifier,
    shape: Shape, online: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick, modifier, (true), shape,
        cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            SpaceBetween, CenterVertically
        ) {
            Text(
                label, Modifier, colorScheme.tertiary,
                style = typography.bodyMedium
            )
            Row(Modifier, Start, CenterVertically) {
                if(text.isNotBlank())
                    Text(
                        text, Modifier, if(online)
                            colorScheme.secondary
                        else colorScheme.primary,
                        style = typography.bodyMedium,
                    )
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    (null), Modifier, colorScheme.onTertiary
                )
            }
        }
    }
}