package ru.rikmasters.gilty.chat.presentation.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.chat.presentation.model.DemoMessageModel
import ru.rikmasters.gilty.chat.presentation.model.MessageModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.timeClock
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ChatRowPreview() {
    GiltyTheme {
        ChatRowContent(
            Modifier.padding(16.dp),
            DemoFullMeetingModel,
            DemoMessageModel,
            shapes.medium,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatRowContent(
    modifier: Modifier = Modifier,
    meeting: FullMeetingModel,
    lastMessage: MessageModel,
    shape: Shape,
    onClick: (() -> Unit)? = null
) {
    Card(
        { onClick?.let { it() } },
        modifier.fillMaxWidth(), (true), shape,
        cardColors(colorScheme.primaryContainer)
    ) {
        Box(Modifier.fillMaxWidth()) {
            if (!todayControl(meeting.dateTime)) Timer(
                meeting.dateTime,
                meeting.isOnline,
                Modifier
                    .align(TopEnd)
                    .padding(12.dp)
            )
            Text(
                lastMessage.createdAt.timeClock(),
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
                Avatar(
                    meeting.organizer.avatar, (true)
                )
                Message(
                    meeting, lastMessage,
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
                if (isOnline) green()
                else red()
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
    meet: FullMeetingModel,
    message: MessageModel,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            meet.title, Modifier,
            colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = SemiBold
        )
        val user = meet.organizer
        Row(Modifier, Start, CenterVertically) {
            Text(
                "${user.username}, ${user.age}",
                Modifier, colorScheme.tertiary,
                style = typography.labelSmall,
                fontWeight = SemiBold
            )
            AsyncImage(
                user.emoji.path, (null),
                Modifier
                    .padding(6.dp)
                    .size(18.dp)
            )
        }
        Text(
            message.text, Modifier,
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
                    if (unRead) colorScheme.primary
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