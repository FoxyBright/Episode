package ru.rikmasters.gilty.translation.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
private fun CustomSnackbar(
    text: String,
    iconPainter: Painter
) {
    Surface(
        color = ThemeExtra.colors.thirdOpaqueGray,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = 6.dp,
                horizontal = 16.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = "",
                tint = ThemeExtra.colors.white
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = ThemeExtra.colors.white
            )
        }
    }
}

@Composable
fun MicroInactiveSnackbar(organizer: Boolean) {
    CustomSnackbar(
        text = stringResource(id = if (organizer) R.string.translations_micro_off_organizer else R.string.translations_micro_off_viewer),
        iconPainter = painterResource(id = R.drawable.ic_micro_inactive)
    )
}

@Composable
fun TranslationResumedSnackbar() {
    CustomSnackbar(
        text = stringResource(id = R.string.translation_resumed),
        iconPainter = painterResource(id = R.drawable.ic_play)
    )
}

@Composable
fun WeakConnectionSnackbar() {
    CustomSnackbar(
        text = stringResource(id = R.string.translations_weak_connection),
        iconPainter = painterResource(id = R.drawable.ic_weak_connection)
    )
}

@Composable
fun TranslationExtendedSnackbar() {
    CustomSnackbar(
        text = stringResource(id = R.string.translation_extended),
        iconPainter = painterResource(id = R.drawable.ic_add)
    )
}