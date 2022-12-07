package ru.rikmasters.gilty.chat.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ChatAppBarPreview() {
    GiltyTheme {
        ChatAppBarContent(
            ChatAppBarState(
                "Бэтмен",
                DemoAvatarModel,
                4
            )
        )
    }
}

data class ChatAppBarState(
    val name: String,
    val avatar: AvatarModel,
    val memberCount: Int
)

interface ChatAppBarCallback {
    fun onBack() {}
    fun onAvatarClick() {}
    fun onKebabClick() {}
}

@Composable
fun ChatAppBarContent(
    state: ChatAppBarState,
    modifier: Modifier = Modifier,
    callback: ChatAppBarCallback? = null
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(20.dp, 14.dp)
            .padding(top = 10.dp),
        Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton({ callback?.onBack() }) {
                Icon(
                    painterResource(R.drawable.ic_back), (null),
                    Modifier.size(24.dp), MaterialTheme.colorScheme.tertiary
                )
            }
            Information(state, Modifier.padding(start = 10.dp))
            { callback?.onAvatarClick() }
        }
        IconButton({ callback?.onKebabClick() }) {
            Icon(
                painterResource(R.drawable.ic_kebab), (null),
                Modifier.size(16.dp), MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun Information(
    state: ChatAppBarState,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            state.avatar.id,
            stringResource(R.string.meeting_avatar),
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable { onClick?.let { it() } },
            contentScale = ContentScale.Crop
        )
        Column {
            Text(
                state.name,
                Modifier.padding(start = 12.dp),
                MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            val count = state.memberCount
            val last = count.toString().last().digitToInt()
            Text(
                "$count ${
                    stringResource(
                        R.string.chats_member_cases,
                        when {
                            last == 1 -> ""
                            last < 5 -> "а"
                            else -> "ов"
                        }
                    )
                }",
                Modifier.padding(start = 12.dp),
                MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}