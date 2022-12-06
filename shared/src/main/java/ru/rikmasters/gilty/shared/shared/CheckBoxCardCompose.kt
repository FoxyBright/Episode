package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
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
fun CheckBoxCard(
    label: String,
    modifier: Modifier = Modifier,
    state: Boolean,
    shape: Shape = MaterialTheme.shapes.large,
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
                .padding(16.dp),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Text(
                label, Modifier, colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium
            )
            TrackCheckBox(
                state, Modifier,
                if(online) colorScheme.secondary
                else colorScheme.primary
            ) { onChange(!state) }
        }
    }
}