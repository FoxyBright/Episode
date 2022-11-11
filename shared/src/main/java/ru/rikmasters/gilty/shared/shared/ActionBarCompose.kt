package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
fun ActionBar(
    title: String,
    details: String? = null,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    Column(modifier.fillMaxWidth()) {
        IconButton({ if (onBack != null) onBack() }, Modifier.padding(top = 16.dp)) {
            Icon(
                painterResource(
                    R.drawable.ic_back
                ),
                stringResource(R.string.action_bar_button_back),
                Modifier
                    .padding(end = 16.dp)
                    .size(24.dp),
                MaterialTheme.colorScheme.tertiary
            )
        }
        Text(
            title,
            Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
        details?.let {
            Text(
                it, Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

@Composable
fun RowActionBar(
    title: String,
    details: String? = null,
    modifier: Modifier = Modifier,
    description: String? = null,
    onBack: (() -> Unit)? = null
) {
    Column(modifier) {
        Row(verticalAlignment = CenterVertically) {
            onBack?.let {
                IconButton(it, Modifier.padding(end = 16.dp)) {
                    Icon(
                        painterResource(R.drawable.ic_back),
                        stringResource(R.string.action_bar_button_back),
                        Modifier.size(24.dp),
                        MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            Text(
                title,
                Modifier.padding(end = 8.dp),
                MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.labelLarge
            )
            details?.let {
                Text(
                    details, style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        description?.let {
            Text(
                it,
                if (onBack != null) Modifier.padding(start = 32.dp)
                else Modifier, MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun BackRowActionBarPreview() {
    GiltyTheme {
        RowActionBar(
            "Заголовок",
            "Детали",
            Modifier.padding(16.dp),
            "Описание"
        ) {}
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun RowActionBarPreview() {
    GiltyTheme {
        RowActionBar(
            "Заголовок",
            "Детали",
            Modifier.padding(16.dp),
            "Описание"
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun ActionBarPreview() {
    GiltyTheme {
        ActionBar(
            "Заголовок",
            "Подробности просиходящего на экране",
            Modifier.padding(16.dp)
        )
    }
}