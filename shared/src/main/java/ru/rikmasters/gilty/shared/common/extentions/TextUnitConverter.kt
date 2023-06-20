package ru.rikmasters.gilty.shared.common.extentions

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import ru.rikmasters.gilty.core.util.composable.getDensity

@Composable
fun Dp.toSp() = with(getDensity()) {
    this@toSp.toSp()
}

@Composable
fun Dp.toPx() = with(getDensity()) {
    this@toPx.toPx()
}