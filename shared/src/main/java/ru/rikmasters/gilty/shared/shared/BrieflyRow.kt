package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign.Companion.TextCenter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.common.profileBadges.getBorderColor
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun BrieflyRowPreview() {
    GiltyTheme {
        val user = DemoProfileModel
        BrieflyRow(
            (user.username ?: ""),
            Modifier.background(colorScheme.background),
            user.avatar?.thumbnail?.url, user.rating.emoji,
        )
    }
}

@Composable
fun BrieflyRow(
    text: String,
    modifier: Modifier = Modifier,
    image: String? = null,
    emoji: EmojiModel? = null,
    textColor: Color = colorScheme.tertiary,
    textStyle: TextStyle = typography.bodyMedium,
    textWeight: FontWeight = SemiBold,
    overflow: TextOverflow = Ellipsis,
    maxLines: Int = 1,
    emojiSize: Int = 18,
    isOnline: Boolean = false,
    group: UserGroupTypeModel = UserGroupTypeModel.DEFAULT
) {
    val label = buildAnnotatedString {
        append("$text "); emoji?.let { appendInlineContent("emoji") }
    }

    Row(modifier, Start, CenterVertically) {
        image?.let {
            UserAvatar(
                modifier = Modifier.padding(end = 12.dp),
                image = image,
                group = group,
                isOnline = isOnline
            )
        }
        Text(
            label, Modifier, textColor,
            style = textStyle, fontWeight = textWeight,
            overflow = overflow, maxLines = maxLines,
            inlineContent = mapOf(
                "emoji" to InlineTextContent(
                    Placeholder(emojiSize.sp, emojiSize.sp, TextCenter)
                ) { GEmojiImage(emoji, Modifier.size(emojiSize.dp)) }
            )
        )
    }
}

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    image: String?,
    group: UserGroupTypeModel? = UserGroupTypeModel.DEFAULT,
    isOnline: Boolean = false,
    imageSize: Int = 38
) {
    val hasGroup = group != UserGroupTypeModel.DEFAULT

    Box(
        modifier = modifier,
        contentAlignment = Center
    ) {
        GCachedImage(
            image, Modifier
                .size(if (hasGroup) (imageSize + 5).dp else imageSize.dp)
                .clip(CircleShape)
                .border(
                    if (hasGroup) 2.dp else 0.dp,
                    (group ?: UserGroupTypeModel.DEFAULT).getBorderColor(),
                    CircleShape
                )
                .border(
                    if (hasGroup) 3.dp else 0.dp,
                    colorScheme.primaryContainer,
                    CircleShape
                ),
            contentScale = Crop
        )
        if (isOnline) {
            OnlineIndicator(modifier = Modifier.align(BottomEnd))
        }
    }
}

@Composable
private fun OnlineIndicator(
    modifier: Modifier
) {
    Surface(
        modifier, CircleShape,
        colorScheme.primaryContainer,
        border = BorderStroke(2.dp, colorScheme.primaryContainer)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color(0xFF35C65A))
        )
    }
}