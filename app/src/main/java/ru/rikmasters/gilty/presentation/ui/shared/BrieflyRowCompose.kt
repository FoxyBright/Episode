package ru.rikmasters.gilty.presentation.ui.shared

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.profile.AvatarModel
import ru.rikmasters.gilty.presentation.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.presentation.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.presentation.model.profile.DemoProfileModel
import ru.rikmasters.gilty.presentation.model.profile.EmojiModel
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

private class UserShortModel(
    val name: String, val age: Int, val emoji: EmojiModel, val avatar: AvatarModel
)

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun BrieflyRowPreview() {
    GiltyTheme {
        BrieflyRowCompose(
            DemoAvatarModel,
            DemoProfileModel.username,
            DemoEmojiModel,
            Modifier.background(MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
fun BrieflyRowCompose(
    avatar: AvatarModel, text: String, emoji: EmojiModel? = null, modifier: Modifier = Modifier
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            avatar.id,
            stringResource(R.string.meeting_avatar),
            Modifier
                .size(38.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text,
                Modifier.padding(start = 12.dp),
                ThemeExtra.colors.mainTextColor,
                style = ThemeExtra.typography.Body1Sb
            )
            emoji?.let {
                AsyncImage(
                    emoji.path, null,
                    Modifier
                        .padding(6.dp)
                        .size(18.dp)
                )
            }
        }
    }
}