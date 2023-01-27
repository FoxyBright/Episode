package ru.rikmasters.gilty.shared.common

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
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.image.EmojiModel.Companion.categoryIcon
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
    onClick: ((Boolean) -> Unit)? = null
) {
    Box(modifier.size((CATEGORY_ELEMENT_SIZE).dp)) {
        Box(
            Modifier
                .size(CATEGORY_ELEMENT_SIZE.dp)
                .clip(CircleShape)
                .background(
                    if(state) color
                    else colorScheme.outlineVariant
                )
                .align(BottomCenter)
                .clickable { onClick?.let { it(!state) } },
            Center
        ) {
            Text(
                name, Modifier, White,
                style = typography.labelSmall,
                textAlign = TextAlign.Center,
                fontWeight = Bold
            )
        }
        Box(
            Modifier
                .size((CATEGORY_ELEMENT_SIZE / 3).dp)
                .clip(CircleShape)
                .background(colorScheme.primaryContainer)
                .align(
                    if(iconBottomState)
                        BottomStart else TopEnd
                ), Center
        ) {
            GEmojiImage(
                icon, Modifier.size(
                    (CATEGORY_ELEMENT_SIZE / 6).dp
                )
            )
        }
    }
}