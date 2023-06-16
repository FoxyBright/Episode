package ru.rikmasters.gilty.notifications.presentation.ui.notification.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.core.util.composable.getContext
import ru.rikmasters.gilty.notifications.presentation.ui.notification.NotificationsCallback
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.swipeableRow
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.*
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.DemoInfoNotificationModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Preview
@Composable
private fun InfoNotificationPreview() {
    GiltyTheme {
        NotificationItem(
            NotificationItemState(
                notification = DemoInfoNotificationModel,
                rowState = DragRowState(0f),
                shape = MaterialTheme.shapes.medium,
                duration = "10 ч", emojiList = emptyList()
            )
        )
    }
}

data class NotificationItemState(
    val notification: NotificationModel,
    val rowState: DragRowState,
    val shape: Shape,
    val duration: String,
    val emojiList: List<EmojiModel>,
)

@Composable
fun NotificationItem(
    state: NotificationItemState,
    modifier: Modifier = Modifier,
    callback: NotificationsCallback? = null,
) {
    val notification =
        remember(state.notification) { state.notification }
    
    val type =
        remember(notification.type) { notification.type }
    
    val meet = remember(
        state.notification.parent.meeting
    ) { notification.parent.meeting }
    
    val user = remember(type) {
        when(type) {
            ADMIN_NOTIFICATION, PHOTO_BLOCKED -> null
            RESPOND -> notification.feedback?.respond?.author
            WATCH -> notification.parent.user
            else -> meet?.organizer
        }
    }
    when(type) {
        MEETING_OVER ->
            MeetOverNotification(
                user = user,
                meet = meet,
                type = type,
                notification = notification,
                state = state,
                modifier = modifier,
                callback = callback
            )
        ADMIN_NOTIFICATION, PHOTO_BLOCKED ->
            SystemNotification(
                type = type,
                state = state,
                notification = notification,
                callback = callback
            )
        else -> OtherNotification(
            user = user,
            meet = meet,
            type = type,
            state = state,
            notification = notification,
            modifier = modifier,
            callback = callback
        )
    }
}

@Composable
private fun OtherNotification(
    user: UserModel?,
    meet: MeetingModel?,
    type: NotificationType,
    state: NotificationItemState,
    notification: NotificationModel,
    modifier: Modifier = Modifier,
    callback: NotificationsCallback?,
) {
    TextNotification(
        user = user,
        callback = callback,
        rowState = state.rowState,
        modifier = modifier,
        shape = state.shape,
        emojiRawState = false,
        onSwiped = {
            callback?.onSwiped(notification)
        },
    ) {
        NotificationText(
            organizer = user,
            type = type,
            meet = meet,
            duration = state.duration,
            onMeetClick = {
                callback?.onMeetClick(meet)
            },
            onUserClick = {
                callback?.onUserClick(user, meet)
            }
        )
    }
}

@Composable
private fun SystemNotification(
    type: NotificationType,
    state: NotificationItemState,
    notification: NotificationModel,
    callback: NotificationsCallback?,
) {
    InfoNotification(
        notificationType = type,
        modifier = Modifier,
        rowState = state.rowState,
        message = if(type == PHOTO_BLOCKED)
            stringResource(R.string.notification_meet_PHOTO_BLOCKED)
        else "",
        duration = state.duration,
        shape = state.shape
    ) { callback?.onSwiped(notification) }
}

@Composable
private fun MeetOverNotification(
    user: UserModel?,
    meet: MeetingModel?,
    type: NotificationType,
    notification: NotificationModel,
    state: NotificationItemState,
    modifier: Modifier = Modifier,
    callback: NotificationsCallback?,
) {
    val emoji = remember(
        notification.feedback?.ratings
    ) { notification.feedback?.ratings?.map { it.emoji } }
    TextNotification(
        user = user,
        callback = callback,
        rowState = state.rowState,
        modifier = modifier,
        shape = state.shape,
        emojiRawState = true,
        onSwiped = { callback?.onSwiped(notification) },
        emojiList = (emoji ?: state.emojiList),
        onEmojiClick = {
            callback?.onEmojiClick(
                notification = notification,
                emoji = it,
                userId = meet?.organizer?.id ?: ""
            )
        }
    ) {
        NotificationText(
            organizer = user,
            type = type,
            meet = meet,
            duration = state.duration,
            emoji = emoji,
            onMeetClick = {
                callback?.onMeetClick(meet)
            },
            onUserClick = {
                callback?.onUserClick(user, meet)
            }
        )
    }
}

@Composable
private fun InfoNotification(
    notificationType: NotificationType,
    modifier: Modifier = Modifier,
    rowState: DragRowState,
    message: String,
    duration: String,
    shape: Shape,
    onSwiped: () -> Unit,
) {
    SwipeableContainer(shape, modifier) {
        Row(
            Modifier.swipeableRow(
                rowState, LocalContext.current
            ) { onSwiped() },
            Center, CenterVertically
        ) {
            Column(
                Modifier.background(
                    color = colorScheme
                        .primaryContainer,
                    shape = shape
                )
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 16.dp),
                    Start, CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            drawable.ic_information
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 24.dp)
                            .size(24.dp),
                        tint = colorScheme.primary
                    )
                    NotificationText(
                        organizer = null,
                        type = notificationType,
                        meet = null,
                        duration = duration,
                        modifier = Modifier
                            .padding(end = 20.dp),
                        notification = message
                    )
                }
            }
        }
    }
}

@Composable
private fun TextNotification(
    user: UserModel?,
    callback: NotificationsCallback?,
    rowState: DragRowState,
    modifier: Modifier = Modifier,
    shape: Shape,
    emojiRawState: Boolean,
    onSwiped: () -> Unit,
    emojiList: List<EmojiModel> = emptyList(),
    onEmojiClick: ((EmojiModel) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var isBackgroundVisible by remember {
        mutableStateOf(false)
    }
    
    LaunchedEffect(
        rowState.offset.targetValue.x
    ) {
        isBackgroundVisible = rowState.offset
            .targetValue.x != 0.0f
    }
    
    SwipeableContainer(
        shape = shape,
        modifier = modifier,
        isVisible = isBackgroundVisible
    ) {
        Row(
            Modifier.swipeableRow(
                state = rowState,
                context = getContext()
            ) { onSwiped() },
            Center, CenterVertically
        ) {
            Notification(
                user = user,
                shape = shape,
                onEmojiClick = onEmojiClick,
                callback = callback,
                emojiRawState = emojiRawState,
                emojiList = emojiList,
                content = content
            )
        }
    }
}

@Composable
private fun Notification(
    user: UserModel?,
    shape: Shape,
    onEmojiClick: ((EmojiModel) -> Unit)?,
    callback: NotificationsCallback?,
    emojiRawState: Boolean,
    emojiList: List<EmojiModel>,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.background(
            color = colorScheme
                .primaryContainer,
            shape = shape
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp, 6.dp)
                .padding(top = 6.dp),
            Start, CenterVertically
        ) {
            UserAvatar(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable {
                        callback?.onUserClick(user)
                    },
                image = user?.avatar?.thumbnail
                    ?.url ?: "",
                group = user?.group,
            )
            content.invoke()
        }
        if(emojiRawState) EmojiRow(
            emojiList = emojiList,
            modifier = Modifier.padding(
                start = 60.dp,
                end = 20.dp
            )
        ) { emoji -> onEmojiClick?.let { it(emoji) } }
    }
}

@Composable
private fun SwipeableContainer(
    shape: Shape,
    modifier: Modifier,
    isVisible: Boolean = true,
    content: @Composable () -> Unit,
) {
    Column(
        modifier.background(
            color = colorScheme.primaryContainer,
            shape = shape
        )
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    color = if(isVisible)
                        colorScheme.primary
                    else Transparent,
                    shape = shape
                )
        ) {
            SwipeableRowBack(Modifier.align(CenterEnd))
            content.invoke()
        }
        if(shape != shapes.mediumBottomRoundedShape
            && shape != MaterialTheme.shapes.medium
        ) GDivider(Modifier.padding(start = 60.dp))
    }
}