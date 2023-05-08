package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun LazyListScope.itemSpacer(
    thickness: Dp = 20.dp,
    horizontal: Boolean = false,
) {
    item {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(
                    if(horizontal) 0.dp
                    else thickness
                )
                .width(
                    if(horizontal) thickness
                    else 0.dp
                )
        )
    }
}