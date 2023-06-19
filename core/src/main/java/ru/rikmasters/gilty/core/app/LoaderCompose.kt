package ru.rikmasters.gilty.core.app

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec.RawRes
import com.airbnb.lottie.compose.LottieConstants.IterateForever
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import ru.rikmasters.gilty.core.R

@Composable
fun GLoader(
    isLoading: Boolean,
    content: @Composable () -> Unit,
) {
    val factor by animateFloatAsState(
        if(isLoading) 1f else 0f
    )
    Box {
        BlurBox(factor = factor) { content() }
        if(factor > 1E-6) Box(
            Modifier
                .fillMaxSize(0.15f)
                .align(Center)
        ) {
            Loader(
                resource = R.raw.loaging,
                modifier = Modifier
                    .alpha(factor)
                    .align(Center),
            )
        }
    }
}

@Composable
fun Loader(
    resource: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = Fit,
    alignment: Alignment = Center,
    speed: Float = 1f,
    iterations: Int = IterateForever,
    isPlaying: Boolean = true,
    onAnimationComplete: (@Composable () -> Unit)? = null,
) {
    val compositionResult by
    rememberLottieComposition(
        RawRes(resource)
    )
    val progress by
    animateLottieCompositionAsState(
        composition = compositionResult,
        isPlaying = isPlaying,
        iterations = iterations,
        speed = speed
    )
    
    onAnimationComplete?.let {
        if(progress == 1f) it()
    }
    
    LottieAnimation(
        composition = compositionResult,
        progress = { progress },
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale
    )
}

@Composable
fun BlurBox(
    modifier: Modifier = Modifier,
    factor: Float = 1f,
    content: @Composable BoxScope.() -> Unit,
) {
    if(Build.VERSION.SDK_INT < 31)
        Box(modifier) {
            content()
            Box(
                modifier
                    .fillMaxSize()
                    .alpha(factor * 0.5f)
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    else
        Box(
            modifier
                .fillMaxSize()
                .blur(
                    (factor * 10).dp,
                    BlurredEdgeTreatment.Unbounded
                )
        ) { content() }
}