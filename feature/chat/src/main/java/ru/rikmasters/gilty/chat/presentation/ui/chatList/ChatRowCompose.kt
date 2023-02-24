package ru.rikmasters.gilty.chat.presentation.ui.chatList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
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
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.extentions.Month.Companion.displayRodName
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.enumeration.ChatNotificationType
import ru.rikmasters.gilty.shared.model.enumeration.ChatNotificationType.*
import ru.rikmasters.gilty.shared.model.enumeration.ChatStatus.COMPLETED
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.MESSAGE
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.NOTIFICATION
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.BrieflyRow
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
            DemoChatModel.copy(
                status = COMPLETED
            ),
            shapes.medium,
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
            DemoChatModel.copy(
                datetime = TOMORROW,
                unreadCount = 10
            ),
            shapes.medium,
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
            DemoChatModel.copy(
                datetime = NOW_DATE,
                unreadCount = 10
            ),
            shapes.medium,
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
            DemoChatModel.copy(
                datetime = NOW_DATE,
                isOnline = true
            ),
            shapes.medium,
            Modifier.padding(16.dp),
        )
    }
}

@Composable
fun SwipeableChatRow(
    state: DragRowState, chat: ChatModel,
    shape: Shape, modifier: Modifier = Modifier,
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
                Modifier, chat, shape,
            ) { onClick?.let { it(chat) } }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChatRowContent(
    modifier: Modifier = Modifier,
    chat: ChatModel, shape: Shape,
    onClick: (() -> Unit)? = null,
) {
    Card(
        { onClick?.let { it() } },
        modifier.fillMaxWidth(), (true), shape,
        cardColors(colorScheme.primaryContainer)
    ) {
        Box(Modifier.fillMaxWidth()) {
            when {
                todayControl(chat.datetime) -> Timer(
                    chat.datetime, chat.isOnline,
                    Modifier
                        .align(TopEnd)
                        .padding(12.dp)
                )
                
                LocalDate.of(chat.datetime)
                    .isBefore(LOCAL_DATE) -> {
                    val date = LocalDate
                        .of(chat.datetime)
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
                    .padding(12.dp)
            ) {
                Avatar(
                    chat.organizer.avatar,
                    chat.unreadCount,
                    chat.isOnline,
                    (chat.meetStatus == MeetStatusType.COMPLETED)
                )
                Message(
                    chat.title, chat.lastMessage,
                    chat.organizer,
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
    modifier: Modifier = Modifier,
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
    message: MessageModel,
    organizer: UserModel,
    modifier: Modifier = Modifier,
) {
    val user = when(message.type) {
        MESSAGE -> message.message?.author!!
        NOTIFICATION -> message.notification?.member ?: organizer
    }
    
    Column(modifier) {
        Text(
            title, Modifier,
            colorScheme.tertiary,
            style = typography.bodyMedium,
            fontWeight = SemiBold
        )
        BrieflyRow(
            "${user.username}, ${user.age}",
            emoji = user.emoji,
            textStyle = typography.labelSmall,
        )
        val attach =
            message.message?.attachments
        Text(
            if(message.type == MESSAGE) {
                if(!attach.isNullOrEmpty())
                    attach.last().type.value
                else message.message?.text!!
            } else notificationText(
                message.notification?.type!!,
                user.gender!!
            ), Modifier.fillMaxWidth(0.8f),
            maxLines = 1,
            color = colorScheme.onTertiary,
            style = typography.labelSmall,
            overflow = Ellipsis
        )
    }
}

@Composable
private fun notificationText(
    type: ChatNotificationType,
    userGender: GenderType,
): String {
    return stringResource(
        when(type) {
            TRANSLATION_COMPLETED -> R.string.chats_message_translation_completed
            TRANSLATION_START_30 -> R.string.chats_message_translation_30
            TRANSLATION_START_5 -> R.string.chats_message_translation_5
            TRANSLATION_STARTED -> R.string.chats_message_translation_started
            CHAT_CREATED -> R.string.chats_message_create_chat
            MEMBER_SCREENSHOT -> R.string.chats_message_make_screenshot
            MEMBER_LEAVE -> R.string.chats_message_leave_meet
            MEMBER_JOIN -> R.string.chats_message_join_meet
        }, when(type) {
            CHAT_CREATED -> "л"
            MEMBER_SCREENSHOT, MEMBER_LEAVE ->
                if(userGender == FEMALE) "a" else ""
            
            MEMBER_JOIN -> if(userGender == FEMALE)
                "aсь" else "ся"
            
            else -> ""
        }
    )
}

@Composable
private fun Avatar(
    avatar: AvatarModel?,
    unRead: Int,
    isOnline: Boolean,
    isConfirm: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        AsyncImage(
            avatar?.thumbnail?.url,
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
                        if(isConfirm)
                            colors.chipGray
                        else if(isOnline)
                            colorScheme.secondary
                        else colorScheme.primary,
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
                    ), if(isConfirm)
                        colorScheme.onTertiary
                    else White, fontWeight = SemiBold,
                    style = typography.headlineSmall
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