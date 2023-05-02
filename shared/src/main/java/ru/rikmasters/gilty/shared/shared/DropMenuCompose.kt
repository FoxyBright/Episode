package ru.rikmasters.gilty.shared.shared

import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

val METRICS: DisplayMetrics = Resources.getSystem().displayMetrics
val screenWidth = METRICS.widthPixels / METRICS.density
@Suppress("unused")
val screenHeight = METRICS.heightPixels / METRICS.density

@Composable
fun GDropMenu(
    menuState: Boolean,
    collapse: () -> Unit,
    offset: DpOffset = DpOffset(
        ((METRICS.widthPixels / METRICS.density) - 200).dp, 0.dp
    ),
    menuItem: List<Pair<String, () -> Unit>>,
) {
    DropdownMenu(
        menuState, collapse,
        Modifier.background(
            colorScheme.primaryContainer
        ), offset
    ) {
        menuItem.forEach {
            DropdownMenuItem(
                { MenuItem(it.first) },
                { it.second.invoke() }
            )
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