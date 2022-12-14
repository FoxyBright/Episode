package ru.rikmasters.gilty.chat.presentation.ui.chatlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.extentions.Month.Companion.displayRodName
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.meeting.OrganizerModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ChatRowLastPreview() {
    GiltyTheme {
        ChatRowContent(
            Modifier.padding(16.dp),
            getChatWithData(dateTime = "2022-11-28T20:00:54.140Z"),
            shapes.medium,
        )
    }
}

@Preview
@Composable
private fun ChatRowActualPreview() {
    GiltyTheme {
        ChatRowContent(
            Modifier.padding(16.dp),
            getChatWithData(
                dateTime = TOMORROW,
                hasUnread = true
            ),
            shapes.medium,
        )
    }
}

@Preview
@Composable
private fun ChatRowTodayPreview() {
    GiltyTheme {
        ChatRowContent(
            Modifier.padding(16.dp),
            getChatWithData(
                dateTime = NOW_DATE,
                hasUnread = true
            ),
            shapes.medium,
        )
    }
}

@Preview
@Composable
private fun ChatRowOnlinePreview() {
    GiltyTheme {
        ChatRowContent(
            Modifier.padding(16.dp),
            getChatWithData(
                dateTime = NOW_DATE,
                isOnline = true
            ),
            shapes.medium,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatRowContent(
    modifier: Modifier = Modifier,
    chat: ChatModel,
    shape: Shape,
    onClick: (() -> Unit)? = null
) {
    Card(
        { onClick?.let { it() } },
        modifier.fillMaxWidth(), (true), shape,
        cardColors(colorScheme.primaryContainer)
    ) {
        Box(Modifier.fillMaxWidth()) {
            when {
                todayControl(chat.dateTime) -> Timer(
                    chat.dateTime, chat.isOnline,
                    Modifier
                        .align(TopEnd)
                        .padding(12.dp)
                )
                
                LocalDate.of(chat.dateTime)
                    .isBefore(LOCAL_DATE) -> {
                    val date = LocalDate.of(chat.dateTime)
                    Text(
                        "${date.day()} ${
                            Month.of(date.month())
                                .displayRodName()
                        }", Modifier
                            .align(TopEnd)
                            .padding(top = 14.dp, end = 12.dp),
                        colorScheme.tertiary, fontWeight = SemiBold,
                        style = typography.labelSmall
                    )
                }
            }
            Text(
                chat.lastMessage.createdAt.timeClock(),
                Modifier
                    .align(BottomEnd)
                    .padding(12.dp),
                colorScheme.onTertiary,
                style = typography.labelSmall
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                Start, CenterVertically
            ) {
                Avatar(chat.organizer.avatar, chat.hasUnread)
                Message(
                    chat.title, chat.organizer,
                    chat.lastMessage.text,
                    Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun Timer(
    time: String,
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.background(
            linearGradient(
                if(isOnline) listOf(
                    colorScheme.secondary,
                    colorScheme.secondary,
                ) else red()
            ), shapes.extraSmall
        )
    ) {
        Text(
            time.timeClock(),
            Modifier.padding(12.dp, 6.dp),
            Color.White, style = typography.labelSmall
        )
    }
}

@Composable
private fun Message(
    title: String,
    user: OrganizerModel,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            title, Modifier,
            colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = SemiBold
        )
        Row(Modifier, Start, CenterVertically) {
            Text(
                "${user.username}, ${user.age}",
                Modifier, colorScheme.tertiary,
                style = typography.labelSmall,
                fontWeight = SemiBold
            )
            Image(
                if(user.emoji.type == "D")
                    painterResource(user.emoji.path.toInt())
                else rememberAsyncImagePainter
                    (user.emoji.path), (null),
                Modifier
                    .padding(6.dp)
                    .size(18.dp)
            )
        }
        Text(
            message, Modifier,
            colorScheme.onTertiary,
            style = typography.labelSmall
        )
    }
}

@Composable
private fun Avatar(
    avatar: AvatarModel,
    unRead: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(modifier, Start, CenterVertically) {
        Box(
            Modifier
                .size(8.dp)
                .background(
                    if(unRead) colorScheme.primary
                    else colorScheme.primaryContainer,
                    CircleShape
                )
        )
        AsyncImage(
            avatar.id,
            stringResource(R.string.meeting_avatar),
            Modifier
                .padding(start = 4.dp)
                .size(52.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}