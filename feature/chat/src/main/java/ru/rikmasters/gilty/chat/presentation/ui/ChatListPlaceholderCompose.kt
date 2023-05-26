package ru.rikmasters.gilty.chat.presentation.ui

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Black
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.categoryIcon
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.flyingDollar
import ru.rikmasters.gilty.shared.shared.GEmojiImage

@Composable
fun ChatListPlaceholder(
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        if(!isSystemInDarkTheme())
            ShadowCircle(Modifier.align(Center))
        Box {
            Column(
                Modifier
                    .offset(y = 200.dp)
                    .padding(horizontal = 6.dp)
                    .align(Center)
            ) {
                Category()
                Text(
                    text = stringResource(R.string.chat_list_placeholder_create_label),
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = -(9).dp),
                    style = typography.bodyMedium.copy(
                        color = Color(0xFFC4C4C4),
                        fontSize = 16.dp.toSp(),
                        fontWeight = SemiBold,
                        textAlign = TextAlign.Center
                    )
                )
            }
            MeetCard(
                modifier = Modifier.offset(x = -(20).dp),
                rotate = -5f,
                bottomColor = if(isSystemInDarkTheme())
                    Color(0xFF212121)
                else Color(0xFFFAFAFA),
                shadow = if(isSystemInDarkTheme())
                    Color(0xFF303030)
                else Color(0xFFEBEBEB)
            )
            MeetCard(
                modifier = Modifier.offset(x = 20.dp),
                rotate = 3f,
                bottomColor = colorScheme.primaryContainer,
                componentsBottom = true
            )
        }
    }
}

@Composable
private fun ShadowCircle(
    modifier: Modifier,
) {
    val size = with(LocalDensity.current) {
        Resources
            .getSystem().displayMetrics
            .let { it.widthPixels / it.density }
            .dp.toPx()
    }
    Box(
        modifier
            .offset(y = 250.dp)
            .size(size.dp)
            .background(
                radialGradient(
                    colors = listOf(
                        Color(0xFFEBEBEB),
                        colorScheme.background,
                    ),
                    center = Offset(
                        x = size / 2,
                        y = size / 2
                    )
                ),
                CircleShape
            )
    )
}

@Composable
private fun MeetCard(
    modifier: Modifier = Modifier,
    rotate: Float = 0f,
    bottomColor: Color = colorScheme.primaryContainer,
    componentsBottom: Boolean = false,
    shadow: Color = if(isSystemInDarkTheme())
        Color(0xFF303030)
    else Color(0xFFF7F7F7),
) {
    Box(
        modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = rotate
            }, Center
    ) {
        Box {
            CardImage()
            BottomCard(
                modifier = Modifier
                    .align(BottomCenter),
                components = componentsBottom,
                background = bottomColor,
                shadow = shadow
            )
        }
    }
}

@Composable
private fun ShadowBack(
    modifier: Modifier,
    colors: List<Color>,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(verticalGradient(colors))
    )
}

@Composable
private fun BottomCard(
    modifier: Modifier = Modifier,
    components: Boolean,
    background: Color,
    shadow: Color,
) {
    Column(modifier) {
        ShadowBack(
            modifier = Modifier
                .width(243.dp)
                .offset(y = 30.dp),
            colors = listOf(
                colorScheme.primaryContainer,
                shadow
            )
        )
        Box(
            Modifier
                .size(243.dp, 101.dp)
                .background(
                    color = background,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            if(components) Row(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp, 10.dp),
                SpaceBetween, CenterVertically
            ) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    SpaceBetween, Start
                ) {
                    MeetName()
                    RespondButton()
                }
                Spacer(Modifier.width(5.dp))
                Column(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    SpaceBetween, End
                ) {
                    CategoriesRow()
                    RespondButton()
                }
            }
        }
    }
}

@Composable
private fun CardImage(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .size(243.dp, 344.dp)
            .background(
                color = colorScheme.primaryContainer,
                shape = RoundedCornerShape(17.dp)
            )
    )
}

@Composable
private fun RespondButton(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.background(
            color = colorScheme.primary,
            shape = RoundedCornerShape(30.dp)
        )
    ) {
        Row(
            Modifier.padding(14.dp, 7.dp),
            Arrangement.Center,
            CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_heart
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 3.dp)
                    .size(14.dp),
                tint = White
            )
            Text(
                text = stringResource(R.string.chat_list_placeholder_respond_button_label),
                modifier = modifier,
                style = typography.labelSmall.copy(
                    color = White,
                    fontSize = 11.dp.toSp(),
                    fontWeight = SemiBold
                )
            )
        }
    }
}

@Composable
private fun MeetName(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.chat_list_placeholder_category_name),
        modifier = modifier,
        style = typography.bodyMedium.copy(
            color = colorScheme.tertiary,
            fontSize = 14.dp.toSp(),
            fontWeight = Bold
        )
    )
}

@Composable
private fun CategoriesRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.background(
            color = colorScheme.background,
            shape = RoundedCornerShape(6.dp)
        )
    ) {
        GEmojiImage(
            emoji = categoryIcon(
                icon = "COCKTAIL"
            ),
            modifier = Modifier
                .padding(5.dp)
                .size(14.dp)
        )
        GEmojiImage(
            emoji = flyingDollar,
            modifier = Modifier
                .padding(end = 5.dp)
                .padding(vertical = 5.dp)
                .size(14.dp)
        )
    }
}

@Composable
private fun Category(
    modifier: Modifier = Modifier,
) {
    val size = 120
    Box(modifier.size((size).dp)) {
        Box(
            Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(colorScheme.primary)
                .align(BottomCenter),
            Center
        ) {
            Text(
                text = stringResource(R.string.chat_list_placeholder_category_name),
                color = White,
                style = typography.labelSmall,
                textAlign = TextAlign.Center,
                fontWeight = Black
            )
        }
        Box(
            modifier = Modifier
                .size((size / 3).dp)
                .clip(CircleShape)
                .background(White)
                .align(BottomStart),
            contentAlignment = Center
        ) {
            GEmojiImage(
                emoji = categoryIcon("COCKTAIL"),
                modifier = Modifier.size((size / 6).dp)
            )
        }
    }
}