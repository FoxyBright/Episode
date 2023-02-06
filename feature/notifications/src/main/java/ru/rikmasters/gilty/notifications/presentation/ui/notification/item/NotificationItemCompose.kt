package ru.rikmasters.gilty.notifications.presentation.ui.notification.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.getDifferenceOfTime
import ru.rikmasters.gilty.shared.common.extentions.swipeableRow
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.LEAVE_EMOTIONS
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.MEETING_OVER
import ru.rikmasters.gilty.shared.model.notification.*
import ru.rikmasters.gilty.shared.shared.EmojiRow
import ru.rikmasters.gilty.shared.shared.SwipeableRowBack

@Preview
@Composable
private fun LEAVE_EMOTION() {
    Item(
        NotificationItemState(
            DemoNotificationLeaveEmotionModel,
            DragRowState(1f),
            MaterialTheme.shapes.medium,
            getDifferenceOfTime(DemoNotificationLeaveEmotionModel.date)
        )
    )
}

@Preview
@Composable
private fun MEETING_OVER() {
    Item(
        NotificationItemState(
            DemoNotificationMeetingOverModel,
            DragRowState(1f),
            MaterialTheme.shapes.medium,
            getDifferenceOfTime(DemoNotificationLeaveEmotionModel.date)
        )
    )
}

@Preview
@Composable
private fun RESPOND_ACCEPT() {
    Item(
        NotificationItemState(
            DemoTodayNotificationRespondAccept,
            DragRowState(1f),
            MaterialTheme.shapes.medium,
            getDifferenceOfTime(DemoNotificationLeaveEmotionModel.date)
        )
    )
}

data class NotificationItemState(
    val notification: NotificationModel,
    val rowState: DragRowState,
    val shape: Shape,
    val duration: String
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Item(
    state: NotificationItemState,
    modifier: Modifier = Modifier,
    onClick: ((NotificationModel) -> Unit)? = null,
    onEmojiClick: ((EmojiModel) -> Unit)? = null,
    onSwiped: (() -> Unit)? = null,
) {
    val notify = state.notification
    Box(
        modifier
            .fillMaxWidth()
            .background(colorScheme.primary, state.shape)
    ) {
        SwipeableRowBack(Modifier.align(CenterEnd))
        Row(
            Modifier.swipeableRow(
                state.rowState, LocalContext.current
            ) { onSwiped?.let { it() } },
            Center, CenterVertically
        ) {
            Card(
                { onClick?.let { it(notify) } },
                Modifier.fillMaxWidth(), true, state.shape,
                cardColors(colorScheme.primaryContainer)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    Start, CenterVertically
                ) {
                    AsyncImage(
                        notify.meeting.organizer?.avatar?.id,
                        stringResource(R.string.meeting_avatar),
                        Modifier
                            .padding(12.dp, 8.dp)
                            .clip(CircleShape)
                            .size(40.dp),
                        painterResource(R.drawable.ic_image_box),
                        contentScale = Crop
                    )
                    NotificationText(
                        notify.meeting.organizer, notify.type,
                        notify.meeting, state.duration, Modifier
                            .padding(end = 20.dp, top = 12.dp)
                    )
                }
                if(notify.type == MEETING_OVER || notify.type == LEAVE_EMOTIONS)
                    EmojiRow(Modifier.padding(start = 60.dp, end = 20.dp))
                    { emoji -> onEmojiClick?.let { it(emoji) } }
            }
        }
    }
}
