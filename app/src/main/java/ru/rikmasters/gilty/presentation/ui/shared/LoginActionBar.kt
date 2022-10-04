package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Composable
fun LoginActionBar(
    title: String,
    details: String = "",
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    Column(modifier.fillMaxWidth()) {
        Image(
            painterResource(R.drawable.ic_back),
            stringResource(R.string.button_back),
            Modifier
                .padding(top = 32.dp)
                .clickable { if (onBack != null) onBack() })
        Text(
            title,
            Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            color = ThemeExtra.colors.mainTextColor
        )
        Text(
            details,
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            style = MaterialTheme.typography.labelSmall,
            color = ThemeExtra.colors.secondaryTextColor
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFE8E8E8)
private fun LoginActionBarPreview() {
    GiltyTheme {
        LoginActionBar(
            "Заголовок",
            "Подробности просиходящего на экране",
            Modifier.padding(16.dp)
        )
    }
}