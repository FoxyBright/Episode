package ru.rikmasters.gilty.notifications.presentation.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Center
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.getDifferenceOfTime
import ru.rikmasters.gilty.shared.common.extentions.swipeableRow
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.LEAVE_EMOTIONS
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType.MEETING_OVER
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationLeaveEmotionModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.shared.model.notification.DemoTodayNotificationRespondAccept
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.shared.EmojiRow

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
        Back(Modifier.align(CenterEnd))
        Row(
            Modifier.swipeableRow(state.rowState) { onSwiped?.let { it() } },
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
                        notify.meeting.organizer.avatar.id,
                        stringResource(R.string.meeting_avatar),
                        Modifier
                            .padding(12.dp, 8.dp)
                            .clip(CircleShape)
                            .size(40.dp),
                        painterResource(R.drawable.gb),
                        contentScale = ContentScale.FillBounds
                    )
                    NotificationText(
                        notify.meeting.organizer, notify.type,
                        notify.meeting, state.duration, Modifier
                            .padding(end = 20.dp, top = 12.dp)
                    )
                }
                if (notify.type == MEETING_OVER || notify.type == LEAVE_EMOTIONS)
                    EmojiRow(Modifier.padding(start = 60.dp, end = 20.dp))
                    { emoji -> onEmojiClick?.let { it(emoji) } }
            }
        }
    }
}

@Composable
private fun Back(modifier: Modifier = Modifier) {
    Column(
        modifier.padding(8.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        Icon(
            painterResource(R.drawable.ic_trash_can),
            null, Modifier, White
        )
        Text(
            stringResource(R.string.meeting_filter_delete_tag_label),
            Modifier.padding(),
            White, fontWeight = SemiBold,
            style = typography.labelSmall
        )
    }
}

