package ru.rikmasters.gilty.shared.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.categoryIcon
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

const val CATEGORY_ELEMENT_SIZE = 120

@Composable
@ExperimentalMaterial3Api
@Preview
private fun CategoryItemTopPreview() {
    GiltyTheme {
        Box(Modifier.background(colorScheme.background)) {
            CategoryItem(
                "Развлечения",
                categoryIcon("POPCORN"),
                colorScheme.primary, (false),
                Modifier.padding(16.dp), (false)
            )
        }
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
fun CategoryItemBottomPreview() {
    GiltyTheme {
        val iconState = remember { mutableStateOf(true) }
        CategoryItem(
            "Развлечения",
            categoryIcon("POPCORN"),
            colorScheme.primary,
            iconState.value
        ) { iconState.value = it }
    }
}

@Composable
fun CategoryItem(
    name: String,
    icon: EmojiModel,
    color: Color,
    state: Boolean,
    modifier: Modifier = Modifier,
    iconBottomState: Boolean = true,
    size: Int = CATEGORY_ELEMENT_SIZE,
    isAnimating: Boolean = false,
    onClick: ((Boolean) -> Unit)? = null,
) {
    val animatedSize by animateDpAsState(
        targetValue = if(isAnimating) 40.dp else size.dp, animationSpec = tween(
            durationMillis = 100,
            easing = FastOutLinearInEasing
        )
    )
    val animatableSize = animatedSize.value

    val backgroundColor by animateColorAsState(if (state)
        color else colorScheme.outlineVariant,
        animationSpec = tween(100, easing = LinearEasing)
    )

    AnimatedVisibility(
        visible = !isAnimating,
        enter = fadeIn(animationSpec = tween(700)),
        exit = fadeOut(animationSpec = tween(700))
    ) {
        Box(
            modifier
                .padding(4.dp)
                .size((animatableSize).dp)) {
            Box(
                Modifier
                    .size(animatableSize.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .align(BottomCenter)
                    .clickable { onClick?.let { it(!state) } }
                    .animateContentSize(),
                Center
            ) {
                Text(
                    name, Modifier.animateContentSize(), White,
                    style = typography.labelSmall,
                    fontSize = typography.labelSmall.fontSize * (animatableSize / size),
                    textAlign = TextAlign.Center,
                    fontWeight = Bold
                )
            }
            Box(
                Modifier
                    .size((animatableSize / 3).dp)
                    .clip(CircleShape)
                    .background(White)
                    .align(
                        if (iconBottomState)
                            BottomStart else TopEnd
                    ), Center
            ) { GEmojiImage(icon, Modifier.size((animatableSize / 6).dp)) }
        }
    }
}