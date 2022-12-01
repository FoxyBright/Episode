package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Suppress("unused")
private class UserShortModel(
    val name: String, val age: Int, val emoji: EmojiModel, val avatar: AvatarModel
)

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun BrieflyRowPreview() {
    GiltyTheme {
        val user = DemoProfileModel
        BrieflyRow(
            user.avatar, user.username, user.emoji,
            Modifier.background(MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
fun BrieflyRow(
    avatar: AvatarModel? = null,
    text: String,
    emoji: EmojiModel? = null,
    modifier: Modifier = Modifier
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        avatar?.let {
            AsyncImage(
                avatar.id,
                stringResource(R.string.meeting_avatar),
                Modifier
                    .size(38.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text,
                Modifier.padding(start = 12.dp),
                MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            emoji?.let {
                AsyncImage(
                    emoji.path, (null),
                    Modifier
                        .padding(6.dp)
                        .size(18.dp)
                )
            }
        }
    }
}

