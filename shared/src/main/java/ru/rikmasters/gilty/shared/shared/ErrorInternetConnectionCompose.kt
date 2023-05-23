package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.drawable.ic_bad_internet_connection_corgi
import ru.rikmasters.gilty.shared.R.drawable.ic_bad_internet_connection_corgi_dark
import ru.rikmasters.gilty.shared.R.string.internet_error_label
import ru.rikmasters.gilty.shared.R.string.update_button_label
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ErrorInternetConnectionPreview() {
    GiltyTheme {
        ErrorInternetConnection(
            Modifier.background(
                colorScheme.background
            )
        )
    }
}

@Composable
fun ErrorInternetConnection(
    modifier: Modifier = Modifier,
    onUpdate: () -> Unit = { },
) {
    Box(modifier) {
        Image(
            painter = painterResource(
                if(isSystemInDarkTheme())
                    ic_bad_internet_connection_corgi_dark
                else ic_bad_internet_connection_corgi
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
        )
        Information(
            modifier = Modifier
                .fillMaxHeight(0.78f)
                .align(TopCenter),
            onUpdate = onUpdate
        )
    }
}

@Composable
private fun Information(
    modifier: Modifier = Modifier,
    onUpdate: () -> Unit,
) {
    Box(modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .align(BottomCenter),
            Top, CenterHorizontally
        ) {
            Text(
                text = stringResource(internet_error_label),
                modifier = Modifier.padding(bottom = 10.dp),
                style = typography.labelLarge.copy(
                    color = colorScheme.tertiary
                )
            )
            UpdateButton(onUpdate)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun UpdateButton(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = shapes.large,
        colors = cardColors(
            containerColor =
            colorScheme.primary
        )
    ) {
        Box(Modifier, Center) {
            Text(
                text = stringResource(update_button_label),
                modifier = Modifier
                    .padding(12.dp, 8.dp),
                style = typography.labelSmall.copy(
                    color = White,
                    fontWeight = SemiBold
                )
            )
        }
    }
}