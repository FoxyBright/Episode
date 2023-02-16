package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
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
            user.thumbnail.map(), user.rating.emoji
        )
    }
}

@Composable
fun BrieflyRow(
    text: String,
    modifier: Modifier = Modifier,
    avatar: AvatarModel? = null,
    emoji: EmojiModel? = null,
) {
    Row(modifier, Start, CenterVertically) {
        avatar?.let {
            AsyncImage(
                it.thumbnail.url, (null), Modifier
                    .padding(end = 12.dp)
                    .size(38.dp)
                    .clip(CircleShape),
                contentScale = Crop
            )
        }
        Row(Modifier, Start, CenterVertically) {
            Text(
                text, Modifier,
                colorScheme.tertiary,
                style = typography.bodyMedium,
                fontWeight = SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            emoji?.let {
                GEmojiImage(
                    it, Modifier
                        .padding(start = 6.dp)
                        .size(18.dp)
                )
            }
        }
    }
}