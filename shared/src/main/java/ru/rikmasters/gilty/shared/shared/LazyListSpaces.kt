package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun LazyListScope.itemSpacer(
    height: Dp = 20.dp
) {
    item {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(height)
        )
    }
}