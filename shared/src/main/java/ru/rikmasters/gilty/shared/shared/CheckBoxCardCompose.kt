package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun CheckBoxCardPreview() {
    GiltyTheme {
        CheckBoxCard(
            "Описание",
            Modifier.fillMaxWidth(),
            true
        ) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  CheckBoxCard(
    label: String,
    modifier: Modifier = Modifier,
    state: Boolean,
    shape: Shape = shapes.large,
    online: Boolean = false,
    onChange: (Boolean) -> Unit
) {
    Card(
        { onChange(!state) }, modifier, (true), shape,
        CardDefaults.cardColors(colorScheme.primaryContainer)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 10.dp),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                label, Modifier, colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium
            )
            TrackCheckBox(
                Modifier, state,
                if(online) colorScheme.secondary
                else colorScheme.primary
            ) { onChange(!state) }
        }
    }
}