package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

private const val title = "Заголовок"
private const val details = "Детали"
private const val description = "Описание"

@Composable
fun ActionBar(
    title: String,
    details: String? = null,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    Column(modifier.fillMaxWidth()) {
        onBack?.let {
            IconButton(it, Modifier.padding(top = 16.dp)) {
                Icon(
                    painterResource(
                        R.drawable.ic_back
                    ),
                    stringResource(R.string.action_bar_button_back),
                    Modifier.size(24.dp),
                    MaterialTheme.colorScheme.tertiary
                )
            }
        }
        Text(
            title,
            Modifier
                .padding(top = 16.dp, start = 16.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
        details?.let {
            Text(
                it, Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp),
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
fun ClosableActionBar(
    title: String,
    details: String? = null,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    Column(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            onBack?.let {
                IconButton(it) {
                    Icon(
                        painterResource(R.drawable.ic_back),
                        stringResource(R.string.action_bar_button_back),
                        Modifier.size(24.dp),
                        MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            CrossButton(
                Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .size(20.dp)
                    .align(TopEnd)
            ) { onClose?.let { it() } }
        }
        Text(
            title,
            Modifier
                .padding(top = 16.dp, start = 16.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
        details?.let {
            Text(
                it, Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun ClosableActionBarPreview() {
    GiltyTheme {
        ClosableActionBar(
            title, details,
            Modifier.padding(16.dp)
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun ClosableActionBarWithBackPreview() {
    GiltyTheme {
        ClosableActionBar(
            title, details,
            Modifier.padding(16.dp)
        ) {}
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun BackRowActionBarPreview() {
    GiltyTheme {
        RowActionBar(
            title, details,
            Modifier.padding(16.dp),
            description
        ) {}
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun RowActionBarPreview() {
    GiltyTheme {
        RowActionBar(
            title, details,
            Modifier.padding(16.dp),
            description
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun ActionBarPreview() {
    GiltyTheme {
        ActionBar(
            title, details,
            Modifier.padding(16.dp)
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun ActionBarWithBackPreview() {
    GiltyTheme {
        ActionBar(
            title, description,
            Modifier.padding(16.dp)
        ) {}
    }
}