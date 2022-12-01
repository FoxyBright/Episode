package ru.rikmasters.gilty.notifications.presentation.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.notifications.presentation.ui.item.Item
import ru.rikmasters.gilty.notifications.presentation.ui.item.NotificationItemState
import ru.rikmasters.gilty.notifications.presentation.ui.item.NotificationsByDateSeparator
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.getDifferenceOfTime
import ru.rikmasters.gilty.shared.common.extentions.rememberDragRowState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.*
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationLeaveEmotionModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.shared.model.notification.DemoTodayNotificationMeetingOver
import ru.rikmasters.gilty.shared.model.notification.DemoTodayNotificationRespondAccept
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel
import ru.rikmasters.gilty.shared.model.profile.ImageModel
import ru.rikmasters.gilty.shared.shared.LazyItemsShapes
import ru.rikmasters.gilty.shared.shared.NavBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun NotificationsContentPreview() {
    GiltyTheme {
        val list = listOf(
            DemoNotificationLeaveEmotionModel,
            DemoNotificationMeetingOverModel,
            DemoTodayNotificationRespondAccept,
            DemoTodayNotificationMeetingOver,
        )
        NotificationsContent(
            NotificationsState(
                list, DemoFullMeetingModel,
                (3), listOf(), true,
                DemoNotificationLeaveEmotionModel,
                listOf(), listOf()
            )
        )
    }
}

interface NotificationsCallback {
    fun onClick(notification: NotificationModel) {}
    fun onSwiped(notification: NotificationModel) {}
    fun onEmojiClick(emoji: EmojiModel) {}
    fun onRespondsClick() {}
    fun onBlurClick() {}
    fun onParticipantClick(index: Int) {}
    fun onNavBarSelect(point: Int) {}
}

data class NotificationsState(
    val notificationsList: List<NotificationModel> = listOf(),
    val lastRespond: FullMeetingModel,
    val myResponds: Int = 0,
    val stateList: List<NavIconState>,
    val blur: Boolean = false,
    val activeNotification: NotificationModel? = null,
    val participants: List<MemberModel>,
    val participantsWrap: List<Boolean>
)

@Composable
fun NotificationsContent(
    state: NotificationsState,
    modifier: Modifier = Modifier,
    callback: NotificationsCallback? = null
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            stringResource(R.string.notification_screen_name),
            Modifier.padding(top = 80.dp, bottom = 10.dp),
            MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.titleLarge
        )
        Notifications(state,
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f),
            { callback?.onClick(it) },
            { callback?.onRespondsClick() },
            { callback?.onEmojiClick(it) })
        { callback?.onSwiped(it) }
    }
    Box(Modifier.fillMaxSize()) {
        NavBar(
            state.stateList, Modifier.align(Alignment.BottomCenter)
        ) { callback?.onNavBarSelect(it) }
    }
    state.activeNotification?.let {
        if (state.blur) Box(
            Modifier
                .fillMaxSize() /*TODO заменить на блюр*/
                .background(MaterialTheme.colorScheme.background)
                .clickable { callback?.onBlurClick() }
        ) {
            ObserveNotification(
                it, state.participants,
                state.participantsWrap,
                Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Center),
                { callback?.onParticipantClick(it) }
            ) { callback?.onEmojiClick(it) }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Responds(
    size: Int,
    image: ImageModel,
    onClick: () -> Unit
) {
    Card(
        onClick, Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
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
                    image.id, (null), Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    stringResource(R.string.notification_responds_on_user_meetings),
                    Modifier.padding(start = 16.dp),
                    MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "$size", Modifier.padding(12.dp, 6.dp),
                        Color.White, fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    stringResource(R.string.profile_responds_label),
                    Modifier,
                    MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
private fun Notifications(
    state: NotificationsState,
    modifier: Modifier = Modifier,
    onClick: ((NotificationModel) -> Unit)? = null,
    onRespondsClick: (() -> Unit)? = null,
    onEmojiClick: ((EmojiModel) -> Unit)? = null,
    onSwiped: ((NotificationModel) -> Unit)? = null,
) {
    val separator = NotificationsByDateSeparator(state.notificationsList)
    val todayList = separator
        .getTodayList().map { it to rememberDragRowState() }
    val weekList = separator
        .getWeekList().map { it to rememberDragRowState() }
    val earlierList = separator
        .getEarlyList().map { it to rememberDragRowState() }
    LazyColumn(modifier) {
        if (state.myResponds != 0) item {
            Responds(state.myResponds, state.lastRespond.organizer.avatar)
            { onRespondsClick?.let { it() } }
        };if (todayList.isNotEmpty()) {; item { label(R.string.meeting_profile_bottom_today_label) }
        itemsIndexed(todayList) { i, not ->
            Item(i, todayList.size, not, { n -> onClick?.let { it(n) } },
                { e -> onEmojiClick?.let { it(e) } }) { n -> onSwiped?.let { it(n) } }
        }
    }; if (weekList.isNotEmpty()) {; item { label(R.string.notification_on_this_week_label) }
        itemsIndexed(weekList) { i, not ->
            Item(i, weekList.size, not, { n -> onClick?.let { it(n) } },
                { e -> onEmojiClick?.let { it(e) } }) { n -> onSwiped?.let { it(n) } }
        }
    }; if (earlierList.isNotEmpty()) {; item { label(R.string.notification_earlier_label) }
        itemsIndexed(earlierList) { i, not ->
            Item(i, earlierList.size, not, { n -> onClick?.let { it(n) } },
                { e -> onEmojiClick?.let { it(e) } }) { n -> onSwiped?.let { it(n) } }
        }
    }; item { Divider(Modifier, 20.dp, Color.Transparent) }
    }
}

@Composable
private fun Item(
    index: Int, size: Int,
    item: Pair<NotificationModel, DragRowState>,
    onClick: (NotificationModel) -> Unit,
    onEmojiClick: (EmojiModel) -> Unit,
    onSwiped: (NotificationModel) -> Unit,
) {
    Item(
        NotificationItemState(
            item.first, item.second, LazyItemsShapes(index, size),
            getDifferenceOfTime(item.first.date)
        ), Modifier, onClick = { onClick(item.first) },
        onEmojiClick = { onEmojiClick(it) },
        onSwiped = { onSwiped(item.first) })
}

@Composable
private fun label(text: Int) {
    Text(
        stringResource(text), Modifier.padding(vertical = 20.dp),
        MaterialTheme.colorScheme.tertiary,
        style = MaterialTheme.typography.labelLarge
    )
}