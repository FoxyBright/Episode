package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModel
import ru.rikmasters.gilty.shared.model.meeting.ShortCategoryModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

const val CATEGORY_ELEMENT_SIZE = 120

@Composable
@ExperimentalMaterial3Api
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
private fun CategoryItemTopPreview() {
    GiltyTheme {
        val iconState = remember { mutableStateOf(true) }
        CategoryItem(
            DemoShortCategoryModel,
            iconState.value,
            iconBottomState = false
        ) { iconState.value = !it }
    }
}

@Composable
@ExperimentalMaterial3Api
@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
fun CategoryItemBottomPreview() {
    GiltyTheme {
        val iconState = remember { mutableStateOf(true) }
        CategoryItem(
            DemoShortCategoryModel,
            iconState.value
        ) { iconState.value = it }
    }
}

@Composable
fun CategoryItem(
    item: ShortCategoryModel,
    state: Boolean,
    modifier: Modifier = Modifier,
    iconBottomState: Boolean = true,
    onClick: ((Boolean) -> Unit)? = null
) {
    Box(
        modifier.size((CATEGORY_ELEMENT_SIZE + 10).dp)
    ) {
        Box(
            Modifier
                .size(CATEGORY_ELEMENT_SIZE.dp)
                .clip(CircleShape)
                .background(
                    if (state) Color(item.color.toColorInt())
                    else ThemeExtra.colors.grayIcon
                )
                .align(Alignment.BottomCenter)
                .clickable {
                    if (onClick != null) onClick(!state)
                },
            Alignment.Center
        ) {
            Text(
                item.name,
                Modifier,
                Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = ThemeExtra.typography.MediumText
            )
        }
        Box(
            Modifier
                .size((CATEGORY_ELEMENT_SIZE / 3).dp)
                .clip(CircleShape)
                .background(Color.White)
                .align(
                    if (iconBottomState) Alignment.BottomStart
                    else Alignment.TopEnd
                ),
            Alignment.Center
        ) {
            AsyncImage(
                item.emoji.path,
                stringResource(R.string.next_button),
                Modifier.size((CATEGORY_ELEMENT_SIZE / 6).dp),
                painterResource(R.drawable.cinema)
            )
        }
    }
}