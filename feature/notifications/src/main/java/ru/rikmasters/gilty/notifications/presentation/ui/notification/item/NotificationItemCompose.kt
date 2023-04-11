package ru.rikmasters.gilty.notifications.presentation.ui.notification.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.rikmasters.gilty.core.R.drawable
import ru.rikmasters.gilty.notifications.presentation.ui.notification.NotificationsCallback
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.swipeableRow
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.*
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.image.ThumbnailModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.shared.EmojiRow
import ru.rikmasters.gilty.shared.shared.GDivider
import ru.rikmasters.gilty.shared.shared.SwipeableRowBack
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

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
                user?.avatar?.thumbnail, state.rowState,
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
                    onMeetClick = {
                        callback?.onMeetClick(
                            meet ?: MeetingModel()
                        )
                    },
                    onUserClick = {
                        callback?.onUserClick(
                            user ?: UserModel(),
                            meet ?: MeetingModel()
                        )
                    }
                )
            }
        }
        
        ADMIN_NOTIFICATION, PHOTO_BLOCKED -> InfoNotification(
            type, Modifier, state.rowState, if(type == PHOTO_BLOCKED)
                stringResource(R.string.notification_meet_PHOTO_BLOCKED)
            else adminMessage, state.duration, state.shape
        ) { callback?.onSwiped(notification) }
        
        else -> TextNotification(
            user?.avatar?.thumbnail, state.rowState,
            modifier, state.shape, (false),
            { callback?.onSwiped(notification) },
        ) {
            NotificationText(
                user, type, meet,
                state.duration,
                onMeetClick = {
                    callback?.onMeetClick(
                        meet ?: MeetingModel()
                    )
                },
                onUserClick = {
                    callback?.onUserClick(
                        user ?: UserModel(),
                        meet ?: MeetingModel()
                    )
                }
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
                        .padding(12.dp),
                    Start, CenterVertically
                ) {
                    Image(
                        painterResource(drawable.ic_information),
                        (null), Modifier
                            .padding(end = 16.dp)
                            .clip(CircleShape)
                            .size(40.dp),
                        contentScale = Crop
                    )
                    NotificationText(
                        (null), notificationType, (null),
                        duration, Modifier.padding(
                            end = 20.dp, top = 12.dp
                        ), message
                    )
                }
            }
        }
    }
}

@Composable
private fun TextNotification(
    thumbnail: ThumbnailModel?,
    rowState: DragRowState,
    modifier: Modifier = Modifier,
    shape: Shape,
    emojiRawState: Boolean,
    onSwiped: () -> Unit,
    emojiList: List<EmojiModel> = emptyList(),
    onEmojiClick: ((EmojiModel) -> Unit)? = null,
    content: @Composable () -> Unit,
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
                        .padding(12.dp),
                    Start, CenterVertically
                ) {
                    val image = ImageRequest
                        .Builder(LocalContext.current)
                        .data(thumbnail?.url)
                        .allowHardware(false)
                        .build()
                    AsyncImage(
                        image, (null), Modifier
                            .padding(end = 16.dp)
                            .clip(CircleShape)
                            .size(40.dp),
                        contentScale = Crop
                    ); content.invoke()
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
                .background(colorScheme.primary, shape)
        ) {
            SwipeableRowBack(Modifier.align(CenterEnd))
            content.invoke()
        }
        if(shape != shapes.mediumBottomRoundedShape
            && shape != MaterialTheme.shapes.medium
        ) GDivider(Modifier.padding(start = 60.dp))
    }
}
