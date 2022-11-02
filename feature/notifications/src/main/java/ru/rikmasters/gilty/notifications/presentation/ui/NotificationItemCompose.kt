package ru.rikmasters.gilty.notifications.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationLeaveEmotionModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.profile.EmojiList
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Preview
@Composable
fun NotificationItemPreview() {
    NotificationItem(
        NotificationItemState(
            DemoNotificationLeaveEmotionModel,
            DragRowState(600f),
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
fun NotificationItem(
    state: NotificationItemState,
    modifier: Modifier = Modifier,
    onClick: ((NotificationModel) -> Unit)? = null,
    onEmojiClick: ((EmojiModel) -> Unit)? = null,
    onSwiped: (() -> Unit)? = null,
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, state.shape)
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .align(Alignment.CenterEnd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(R.drawable.ic_trash_can),
                null, Modifier, Color.White
            )
            Text(
                stringResource(R.string.meeting_filter_delete_tag_label),
                Modifier.padding(),
                Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            Modifier.swipeableRow(state.rowState) { onSwiped?.let { it() } },
            Arrangement.Center, Alignment.CenterVertically
        ) {
            Card(
                { onClick?.let { it(state.notification) } },
                Modifier.fillMaxWidth(), true, state.shape,
                CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        state.notification.meeting.organizer.avatar.id,
                        stringResource(R.string.meeting_avatar),
                        Modifier
                            .padding(12.dp, 8.dp)
                            .clip(CircleShape)
                            .size(40.dp),
                        painterResource(R.drawable.gb),
                        contentScale = ContentScale.FillBounds
                    )
                    NotificationText(
                        state.notification.meeting.organizer,
                        state.notification.type,
                        state.notification.meeting,
                        state.duration,
                        Modifier
                            .padding(end = 20.dp)
                            .padding(vertical = 12.dp)
                    )
                }
                if (state.notification.type == NotificationType.MEETING_OVER ||
                    state.notification.type == NotificationType.LEAVE_EMOTIONS
                ) {
                    Column(Modifier.padding(start = 60.dp, end = 20.dp)) {
                        Image(
                            painterResource(R.drawable.ic_cloud_part),
                            null, Modifier.padding(start = 60.dp)
                        )
                        LazyRow(
                            Modifier
                                .padding(10.dp)
                                .background(ThemeExtra.colors.chipGray, CircleShape)
                        ) {
                            items(EmojiList) {
                                Image(
                                    if (it.type == "D") painterResource(it.path.toInt())
                                    else rememberAsyncImagePainter(it.path), (null),
                                    Modifier
                                        .padding(10.dp)
                                        .size(20.dp)
                                        .clickable { onEmojiClick?.let { c -> c(it) } }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

