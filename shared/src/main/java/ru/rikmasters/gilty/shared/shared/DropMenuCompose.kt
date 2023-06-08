package ru.rikmasters.gilty.shared.shared

import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.switch

val METRICS: DisplayMetrics = Resources.getSystem().displayMetrics
val density = METRICS.density
val screenWidth = METRICS.widthPixels / density
val screenHeight = METRICS.heightPixels / density

@Composable
fun GDropMenu(
    menuState: Boolean,
    collapse: () -> Unit,
    offset: DpOffset = LocalConfiguration
        .current.screenWidthDp.let {
            DpOffset((it - 200).dp, 0.dp)
        },
    menuItem: List<Pair<String, () -> Unit>>,
) {
    MaterialTheme(
        colorScheme = colorScheme.switch()
            .copy(
                surface = colorScheme.primaryContainer,
                onSurface = colorScheme.primary,
            ),
    ) {
        DropdownMenu(
            expanded = menuState,
            onDismissRequest = collapse,
            modifier = Modifier.background(
                color = colorScheme.primaryContainer,
            ),
            offset = offset,
        ) {
            menuItem.forEach {
                DropdownMenuItem(
                    text = { MenuItem(it.first) },
                    onClick = { it.second.invoke() },
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text, modifier, colorScheme.tertiary,
        style = typography.bodyMedium
    )
}