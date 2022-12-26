package ru.rikmasters.gilty.chat.presentation.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.extentions.Month.Companion.displayRodName
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.meeting.OrganizerModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.GEmojiImage
import ru.rikmasters.gilty.shared.shared.SwipeableRowBack
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
import java.util.Locale.getDefault

@Preview
@Composable
private fun ChatRowLastPreview() {
    GiltyTheme {
        SwipeableChatRow(
            DragRowState(100f),
            getChatWithData(dateTime = "2022-11-28T20:00:54.140Z"),
            shapes.medium, (123),
            Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun ChatRowActualPreview() {
    GiltyTheme {
        SwipeableChatRow(
            DragRowState(100f),
            getChatWithData(
                dateTime = TOMORROW,
                hasUnread = true
            ),
            shapes.medium, (15152),
            Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun ChatRowTodayPreview() {
    GiltyTheme {
        SwipeableChatRow(
            DragRowState(100f),
            getChatWithData(
                dateTime = NOW_DATE,
                hasUnread = true
            ),
            shapes.medium, (13151),
            Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun ChatRowOnlinePreview() {
    GiltyTheme {
        SwipeableChatRow(
            DragRowState(100f),
            getChatWithData(
                dateTime = NOW_DATE,
                isOnline = true
            ),
            shapes.medium, (1241521),
            Modifier.padding(16.dp),
        )
    }
}

@Composable
fun SwipeableChatRow(
    state: DragRowState, chat: ChatModel,
    shape: Shape, unRead: Int,
    modifier: Modifier = Modifier,
    onClick: ((ChatModel) -> Unit)? = null,
    onSwiped: ((ChatModel) -> Unit)? = null,
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(
                if(chat.isOnline)
                    colorScheme.secondary
                else colorScheme.primary, shape
            )
    ) {
        SwipeableRowBack(
            Modifier.align(CenterEnd)
        )
        Row(
            Modifier.swipeableRow(
                state, LocalContext.current
            ) { onSwiped?.let { it(chat) } },
            Center, CenterVertically
        ) {
            ChatRowContent(
                Modifier, chat,
                shape, unRead
            ) { onClick?.let { it(chat) } }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChatRowContent(
    modifier: Modifier = Modifier,
    chat: ChatModel,
    shape: Shape,
    unRead: Int,
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
                    val date = LocalDate
                        .of(chat.dateTime)
                    Text(
                        "${date.day()} ${
                            Month.of(date.month())
                                .displayRodName()
                                .lowercase(getDefault())
                        }", Modifier
                            .align(TopEnd)
                            .padding(
                                top = 14.dp,
                                end = 12.dp
                            ), colorScheme.tertiary,
                        fontWeight = SemiBold,
                        style = typography.labelSmall
                    )
                }
            }
            Text(
                chat.lastMessage
                    .createdAt.timeClock(),
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
                Avatar(chat.organizer.avatar, unRead)
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
            ), RoundedCornerShape(6.dp)
        )
    ) {
        Text(
            time.timeClock(),
            Modifier.padding(12.dp, 4.dp),
            White, style = typography.labelSmall
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
            GEmojiImage(
                user.emoji, Modifier
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
    unRead: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        AsyncImage(
            avatar.id,
            stringResource(R.string.meeting_avatar),
            Modifier
                .padding(start = 4.dp)
                .size(52.dp)
                .clip(CircleShape),
            contentScale = Crop
        )
        if(unRead > 0) Box(
            Modifier
                .offset(6.dp, 6.dp)
                .background(
                    colorScheme.primaryContainer,
                    shapes.medium
                )
                .align(BottomEnd)
        ) {
            Box(
                Modifier
                    .padding(2.dp)
                    .background(
                        colors.chipGray,
                        shapes.medium
                    )
            ) {
                Text(
                    counter("$unRead"),
                    Modifier.padding(
                        when("$unRead".length) {
                            1 -> 10.dp
                            2 -> 6.dp
                            else -> 2.dp
                        }, 2.dp
                    ), colorScheme.onTertiary,
                    style = typography.headlineSmall,
                    fontWeight = SemiBold
                )
            }
        }
    }
}

private fun counter(count: String): String {
    return when(val size = count.length) {
        in 0..3 -> count
        in 4..6 -> count.substring(0, size - 3) +
                ",${count[4]}" + "k"
        
        in 7..9 -> count.substring(0, size - 6) +
                ",${count[4]}" + "m"
        
        else -> ">999m"
    }
}
