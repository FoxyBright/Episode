package ru.rikmasters.gilty.complaints.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.drawable.ic_big_success
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ComplainAlertPreview() {
    GiltyTheme {
        ComplainAlert(
            true,
            Modifier.padding(16.dp)
        ) {}
    }
}

@Composable
fun ComplainAlert(
    state: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    if(state) AlertDialog(
        onDismiss, {}, modifier
            .clickable {
                onClick?.let { it() }
            }, text = {
            Column(
                Modifier,
                Arrangement.Center,
                CenterHorizontally
            ) {
                androidx.compose.material3.Icon(
                    painterResource(ic_big_success),
                    (null), Modifier
                        .padding(top = 12.dp)
                        .size(98.dp),
                    colorScheme.primary
                )
                Text(
                    "Жалоба отправлена",
                    Modifier.padding(top = 20.dp),
                    colorScheme.tertiary,
                    style = typography.bodyMedium,
                    textAlign = Center,
                    fontWeight = Bold
                )
                Text(
                    "Модераторы скоро рассмотрят вашу жалобу",
                    Modifier.padding(top = 2.dp),
                    colorScheme.tertiary,
                    style = typography.labelSmall,
                    textAlign = Center,
                    fontWeight = SemiBold,
                )
            }
        }, containerColor = colorScheme.primaryContainer
    )
}