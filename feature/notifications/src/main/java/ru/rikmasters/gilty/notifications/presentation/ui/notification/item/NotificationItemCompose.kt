package ru.rikmasters.gilty.notifications.presentation.ui.notification.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.notifications.presentation.ui.notification.NotificationsCallback
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.swipeableRow
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.ADMIN_NOTIFICATION
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.MEETING_OVER
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.PHOTO_BLOCKED
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.RESPOND
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.WATCH
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.DemoInfoNotificationModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.shared.EmojiRow
import ru.rikmasters.gilty.shared.shared.GDivider
import ru.rikmasters.gilty.shared.shared.SwipeableRowBack
import ru.rikmasters.gilty.shared.shared.UserAvatar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

data class NotificationItemState(
    val notification: NotificationModel,
    val rowState: DragRowState,
    val shape: Shape,
    val duration: String,
    val emojiList: List<EmojiModel>,
)

@Preview
@Composable
private fun InfoNotificationPreview() {
    GiltyTheme {
        NotificationItem(
            NotificationItemState(
                DemoInfoNotificationModel,
                DragRowState(0f),
                MaterialTheme.shapes.medium,
                "10 ч", emptyList()
            )
        )
    }
}

@Composable
fun NotificationItem(
    state: NotificationItemState,
    modifier: Modifier = Modifier,
    callback: NotificationsCallback? = null,
) {
    val notification = state.notification
    val type = notification.type
    val meet = notification.parent.meeting
    val adminMessage = "ADMIN MESSAGE"
    val user = when(type) {
        ADMIN_NOTIFICATION, PHOTO_BLOCKED -> null
        RESPOND -> notification.feedback?.respond?.author
        WATCH -> notification.parent.user
        else -> meet?.organizer
    }
    
    when(type) {
        
        MEETING_OVER -> {
            val emoji = notification
                .feedback?.ratings?.map { it.emoji }
            TextNotification(
                user, callback, state.rowState,
                modifier, state.shape, (true),
                { callback?.onSwiped(notification) },
                (emoji ?: state.emojiList), {
                    callback?.onEmojiClick(
                        notification, it,
                        meet?.organizer?.id ?: ""
                    )
                }
            ) {
                NotificationText(
                    user, type, meet,
                    state.duration, emoji = emoji,
                    onMeetClick = { callback?.onMeetClick(meet) },
                    onUserClick = { callback?.onUserClick(user, meet) }
                )
            }
        }
        
        ADMIN_NOTIFICATION, PHOTO_BLOCKED -> InfoNotification(
            type, Modifier, state.rowState, if(type == PHOTO_BLOCKED)
                stringResource(R.string.notification_meet_PHOTO_BLOCKED)
            else adminMessage, state.duration, state.shape
        ) { callback?.onSwiped(notification) }
        
        else -> TextNotification(
            user, callback, state.rowState,
            modifier, state.shape, (false),
            { callback?.onSwiped(notification) },
        ) {
            NotificationText(
                user, type, meet,
                state.duration,
                onMeetClick = { callback?.onMeetClick(meet) },
                onUserClick = { callback?.onUserClick(user, meet) }
            )
        }
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
                    colorScheme.primaryContainer,
                    shape
                )
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 16.dp),
                    Start, CenterVertically
                ) {
                    Icon(
                        painterResource(drawable.ic_information),
                        (null), Modifier
                            .padding(start = 8.dp, end = 24.dp)
                            .size(24.dp),
                        colorScheme.primary
                    )
                    NotificationText(
                        (null), notificationType, (null),
                        duration, Modifier.padding(
                            end = 20.dp
                        ), message
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
    
    LaunchedEffect(key1 = rowState.offset.targetValue.x, block = {
        isBackgroundVisible = rowState.offset.targetValue.x != 0.0f
    })
    
    SwipeableContainer(shape, modifier, isBackgroundVisible) {
        
        Row(
            Modifier.swipeableRow(
                rowState, LocalContext.current
            ) { onSwiped() },
            Center, CenterVertically
        ) {
            Column(
                Modifier.background(
                    colorScheme.primaryContainer,
                    shape
                )
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 6.dp)
                        .padding(top = 6.dp),
                    Start, CenterVertically
                ) {
                    UserAvatar(modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable {
                            callback?.onUserClick(user)
                        },
                        image = user?.avatar?.thumbnail?.url,
                        group = user?.group,
                    )
                    content.invoke()
                }
                if(emojiRawState) EmojiRow(
                    emojiList, Modifier.padding(
                        start = 60.dp, end = 20.dp
                    )
                ) { emoji -> onEmojiClick?.let { it(emoji) } }
            }
        }
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
            colorScheme.primaryContainer,
            shape
        )
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    if (isVisible) colorScheme.primary
                    else Color.Transparent,
                    shape
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
