package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.airbnb.lottie.compose.LottieConstants.IterateForever
import ru.rikmasters.gilty.shared.R.raw.find_more

@Preview
@Composable
fun Preview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(colorScheme.primary),
        Center
    ) {
        AnimatedImage(
            find_more,
            Modifier.size(300.dp)
        )
    }
}

@Composable
fun AnimatedImage(
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
        LottieCompositionSpec.RawRes(resource)
    )
    val progress by
    animateLottieCompositionAsState(
        compositionResult, isPlaying,
        iterations = iterations,
        speed = speed
    )
    
    onAnimationComplete?.let {
        if(progress == 1f) it()
    }
    
    LottieAnimation(
        compositionResult,
        { progress }, modifier,
        alignment = alignment,
        contentScale = contentScale
    )
}

/*
    До лучших времен.
    Позволяет менять свойства
    анимации по ключам из JSON
*/
@Composable
@Suppress("unused")
fun <T> dynamicProperties(
    property: T,
    value: T,
    vararg keyPath: String,
): LottieDynamicProperties {
    return rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property, value,
            keyPath = keyPath
        )
    )
}