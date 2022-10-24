package ru.rikmasters.gilty.presentation.ui.shared

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

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
                ThemeExtra.colors.mainTextColor
            )
        }
        Text(
            title,
            Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            color = ThemeExtra.colors.mainTextColor
        )
        details?.let {
            Text(
                it,
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                style = MaterialTheme.typography.labelSmall,
                color = ThemeExtra.colors.secondaryTextColor
            )
        }
    }
}

@Composable
fun RowActionBar(
    title: String,
    details: String? = null,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton({ onBack?.let { it() } }, Modifier.padding(end = 16.dp)) {
            Icon(
                painterResource(R.drawable.ic_back),
                stringResource(R.string.action_bar_button_back),
                Modifier.size(24.dp),
                ThemeExtra.colors.mainTextColor
            )
        }
        Text(
            title,
            Modifier.padding(end = 4.dp),
            ThemeExtra.colors.mainTextColor,
            style = ThemeExtra.typography.H3
        )
        details?.let {
            Text(
                details, style = ThemeExtra.typography.H3,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun RowActionBarPreview() {
    GiltyTheme {
        RowActionBar(
            "Заголовок",
            "Детали",
            Modifier.padding(16.dp)
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun LoginActionBarPreview() {
    GiltyTheme {
        ActionBar(
            "Заголовок",
            "Подробности просиходящего на экране",
            Modifier.padding(16.dp)
        )
    }
}