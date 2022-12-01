package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.meeting.FilterModel

@Composable
fun Element(
    it: FilterModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            Modifier.padding(bottom = 18.dp),
            Start, CenterVertically
        ) {
            Text(
                it.name, Modifier,
                colorScheme.tertiary,
                style = typography.labelLarge
            )
            it.details?.let { details ->
                Text(
                    details, Modifier.padding(start = 4.dp),
                    colorScheme.onTertiary,
                    style = typography.labelSmall
                )
            }
        }
        it.content.invoke()
    }
}