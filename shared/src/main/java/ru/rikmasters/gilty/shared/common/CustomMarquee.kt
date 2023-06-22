package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.marquee(
    spacing:Dp = 14.dp,
    iterations: Int = Int.MAX_VALUE
) = this.then(
    Modifier.basicMarquee(
        spacing = MarqueeSpacing(spacing),
        iterations = iterations
    )
)