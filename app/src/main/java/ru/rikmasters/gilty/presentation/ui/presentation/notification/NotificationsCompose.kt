package ru.rikmasters.gilty.presentation.ui.presentation.notification

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.ShortMeetingModel
import ru.rikmasters.gilty.presentation.model.profile.EmojiModel
import ru.rikmasters.gilty.presentation.model.notification.DemoNotificationLeaveEmotionModel
import ru.rikmasters.gilty.presentation.model.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.presentation.model.notification.DemoTodayNotificationMeetingOver
import ru.rikmasters.gilty.presentation.model.notification.DemoTodayNotificationRespondAccept
import ru.rikmasters.gilty.presentation.model.notification.NotificationModel
import ru.rikmasters.gilty.presentation.ui.shared.LazyItemsShapes
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
fun NotificationsComposePreview() {
    GiltyTheme {
        val context = LocalContext.current
        var responds by remember { mutableStateOf(10) }
        val notificationsList = remember {
            mutableStateListOf(
                DemoNotificationLeaveEmotionModel,
                DemoNotificationMeetingOverModel,
                DemoTodayNotificationRespondAccept,
                DemoTodayNotificationMeetingOver,
            )
        }
        NotificationsCompose(
            Modifier, NotificationsComposeState(notificationsList, DemoMeetingModel, responds),
            object : NotificationsComposeCallback {
                override fun onClick(notification: NotificationModel) {
                    Toast.makeText(context, "Notification!", Toast.LENGTH_SHORT).show()
                }

                override fun onEmojiClick(emoji: EmojiModel) {
                    Toast.makeText(context, "Emoji!", Toast.LENGTH_SHORT).show()
                }

                override fun onRespondsClick() {
                    if (responds > 0) responds -= 1
                }

                override fun onSwiped(notification: NotificationModel) {
                    if (notificationsList.contains(notification))
                        notificationsList.remove(notification)
                }
            })
    }
}

interface NotificationsComposeCallback {
    fun onClick(notification: NotificationModel) {}
    fun onSwiped(notification: NotificationModel) {}
    fun onEmojiClick(emoji: EmojiModel) {}
    fun onRespondsClick() {}
}

data class NotificationsComposeState(
    val notificationsList: List<NotificationModel> = listOf(),
    val lastRespond: ShortMeetingModel,
    val myResponds: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsCompose(
    modifier: Modifier = Modifier,
    state: NotificationsComposeState,
    callback: NotificationsComposeCallback? = null
) {
    val separator = NotificationsByDateSeparator(state.notificationsList)
    val todayList = separator.getTodayList().map { it to rememberDragRowState() }
    val weekList = separator.getWeekList().map { it to rememberDragRowState() }
    val earlierList = separator.getEarlyList().map { it to rememberDragRowState() }
    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            stringResource(R.string.notification_screen_name),
            Modifier.padding(top = 80.dp, bottom = 10.dp),
            ThemeExtra.colors.mainTextColor,
            style = ThemeExtra.typography.H1
        )
        if (state.myResponds != 0)
            Card(
                { callback?.onRespondsClick() },
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(ThemeExtra.colors.cardBackground)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            state.lastRespond.organizer.id, null,
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            stringResource(R.string.notification_responds_on_user_meetings),
                            Modifier.padding(start = 16.dp),
                            color = ThemeExtra.colors.mainTextColor,
                            style = ThemeExtra.typography.SubHeadMedium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                "${state.myResponds}",
                                Modifier.padding(12.dp, 6.dp),
                                Color.White,
                                style = ThemeExtra.typography.SubHeadSb
                            )
                        }
                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            stringResource(R.string.profile_responds_label),
                            Modifier,
                            ThemeExtra.colors.secondaryTextColor
                        )
                    }
                }
            }
        LazyColumn(Modifier.fillMaxSize(), rememberLazyListState()) {
            if (todayList.isNotEmpty()) {
                item { label(stringResource(R.string.meeting_profile_bottom_today_label)) }
                itemsIndexed(todayList) { index, notification ->
                    NotificationItem(
                        NotificationItemState(
                            notification.first, notification.second,
                            LazyItemsShapes(index, todayList.size),
                            getDifferenceOfTime(notification.first.date)
                        ), Modifier, onClick = { callback?.onClick(notification.first) },
                        onEmojiClick = { callback?.onEmojiClick(it) },
                        onSwiped = { callback?.onSwiped(notification.first) })
                }
            }
            if (weekList.isNotEmpty()) {
                item { label(stringResource(R.string.notification_on_this_week_label)) }
                itemsIndexed(weekList) { index, notification ->
                    NotificationItem(
                        NotificationItemState(
                            notification.first, notification.second,
                            LazyItemsShapes(index, weekList.size),
                            getDifferenceOfTime(notification.first.date)
                        ), Modifier, onClick = { callback?.onClick(notification.first) },
                        onEmojiClick = { callback?.onEmojiClick(it) },
                        onSwiped = { callback?.onSwiped(notification.first) })
                }
            }
            if (earlierList.isNotEmpty()) {
                item { label(stringResource(R.string.notification_earlier_label)) }
                itemsIndexed(earlierList) { index, notification ->
                    NotificationItem(
                        NotificationItemState(
                            notification.first, notification.second,
                            LazyItemsShapes(index, earlierList.size),
                            getDifferenceOfTime(notification.first.date)
                        ), Modifier, onClick = { callback?.onClick(notification.first) },
                        onEmojiClick = { callback?.onEmojiClick(it) },
                        onSwiped = { callback?.onSwiped(notification.first) })
                }
            }
        }
    }
}

@Composable
private fun label(text: String) {
    Text(
        text, Modifier.padding(vertical = 20.dp),
        ThemeExtra.colors.mainTextColor,
        style = ThemeExtra.typography.H3
    )
}