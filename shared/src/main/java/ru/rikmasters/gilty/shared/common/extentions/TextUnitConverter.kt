package ru.rikmasters.gilty.shared.common.extentions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*

@Composable
@Suppress("unused")
fun Dp.toSp() = with(LocalDensity.current){
    this@toSp.toSp()
}