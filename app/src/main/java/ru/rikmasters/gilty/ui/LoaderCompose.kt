package ru.rikmasters.gilty.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.FixedScale
import ru.rikmasters.animated.AnimatedImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.BlurBox

@Composable
fun GLoader(isLoading: Boolean, content: @Composable () -> Unit) {
    val factor by animateFloatAsState(
        if(isLoading) 1f else 0f
    )
    Box {
        BlurBox(factor = factor) { content() }
        if(factor > 1E-6) Box(
            Modifier
                .fillMaxSize(0.15f)
                .align(Alignment.Center)
        ) {
            AnimatedImage(
                R.raw.loaging,
                Modifier
                    .alpha(factor)
                    .align(Alignment.Center),
            )
        }
    }
}