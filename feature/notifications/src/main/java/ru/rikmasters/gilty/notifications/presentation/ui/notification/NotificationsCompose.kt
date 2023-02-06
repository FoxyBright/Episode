package ru.rikmasters.gilty.notifications.presentation.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.Item
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItemState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Responds
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.getDifferenceOfTime
import ru.rikmasters.gilty.shared.common.extentions.rememberDragRowState
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationLeaveEmotionModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationModelList
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.shared.NavBar
import ru.rikmasters.gilty.shared.shared.lazyItemsShapes
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun NotificationsContentPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            NotificationsContent(
                NotificationsState(
                    Triple(
                        DemoNotificationModelList,
                        DemoNotificationModelList,
                        DemoNotificationModelList
                    ), DemoMeetingModel,
                    (3), listOf(), (false),
                    DemoNotificationLeaveEmotionModel,
                    listOf(), listOf()
                )
            )
        }
    }
}

@Preview
@Composable
private fun NotificationsBlurPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            NotificationsContent(
                NotificationsState(
                    Triple(
                        DemoNotificationModelList,
                        DemoNotificationModelList,
                        DemoNotificationModelList
                    ), DemoMeetingModel,
                    (3), listOf(), (true),
                    DemoNotificationLeaveEmotionModel,
                    listOf(), listOf()
                )
            )
        }
    }
}

data class NotificationsState(
    val notifications: Triple<List<NotificationModel>,
            List<NotificationModel>, List<NotificationModel>>,
    val lastRespond: MeetingModel,
    val myResponds: Int,
    val stateList: List<NavIconState>,
    val blur: Boolean,
    val activeNotification: NotificationModel?,
    val participants: List<MemberModel>,
    val participantsWrap: List<Boolean>,
)

interface NotificationsCallback {
    
    fun onClick(notification: NotificationModel)
    fun onSwiped(notification: NotificationModel)
    fun onEmojiClick(emoji: EmojiModel, notify: NotificationModel)
    fun onRespondsClick()
    fun onBlurClick()
    fun onParticipantClick(index: Int)
    fun onNavBarSelect(point: Int)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NotificationsContent(
    state: NotificationsState,
    modifier: Modifier = Modifier,
    callback: NotificationsCallback? = null,
) {
    Scaffold(
        modifier
            .fillMaxSize()
            .background(colorScheme.background),
        topBar = {
            Text(
                stringResource(R.string.notification_screen_name),
                Modifier.padding(
                    start = 16.dp,
                    top = 80.dp,
                    bottom = 10.dp
                ), colorScheme.tertiary,
                style = typography.titleLarge
            )
        },
        bottomBar = {
            NavBar(
                state.stateList, Modifier
            ) { callback?.onNavBarSelect(it) }
        }
    ) { padding ->
        Notifications(state,
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            { callback?.onClick(it) },
            { callback?.onRespondsClick() },
            { e, not -> callback?.onEmojiClick(e, not) })
        { callback?.onSwiped(it) }
    }
    state.activeNotification?.let {
        if(state.blur) Box(
            Modifier
                .fillMaxSize() /*TODO заменить на блюр*/
                .background(colorScheme.background)
                .clickable { callback?.onBlurClick() }
        ) {
            ObserveNotification(
                it, state.participants,
                state.participantsWrap,
                Modifier
                    .padding(horizontal = 16.dp)
                    .align(Center),
                { part -> callback?.onParticipantClick(part) }
            ) { e -> callback?.onEmojiClick(e, it) }
        }
    }
}

@Composable
private fun Notifications(
    state: NotificationsState,
    modifier: Modifier = Modifier,
    onClick: (NotificationModel) -> Unit,
    onRespondsClick: () -> Unit,
    onEmojiClick: (EmojiModel, NotificationModel) -> Unit,
    onSwiped: (NotificationModel) -> Unit,
) {
    val notify =
        state.notifications
    val todayList =
        notify.first.map { it to rememberDragRowState() }
    val weekList =
        notify.second.map { it to rememberDragRowState() }
    val earlierList =
        notify.third.map { it to rememberDragRowState() }
    
    LazyColumn(modifier) {
        if(state.myResponds != 0) item {
            
            Responds(
                stringResource(R.string.notification_responds_on_user_meetings),
                state.myResponds, state.lastRespond.organizer?.avatar,
                Modifier.padding(vertical = 12.dp)
            ) { onRespondsClick() }
        }
        
        if(todayList.isNotEmpty()) {
            item { Label(R.string.meeting_profile_bottom_today_label) }
            itemsIndexed(todayList) { i, not ->
                Item(i, todayList.size, not,
                    { onClick(it) },
                    { onEmojiClick(it, not.first) })
                { onSwiped(it) }
            }
        }
        
        if(weekList.isNotEmpty()) {
            item { Label(R.string.notification_on_this_week_label) }
            itemsIndexed(weekList) { i, not ->
                Item(i, weekList.size, not,
                    { onClick(it) },
                    { onEmojiClick(it, not.first) })
                { onSwiped(it) }
            }
        }
        
        if(earlierList.isNotEmpty()) {
            item { Label(R.string.notification_earlier_label) }
            itemsIndexed(earlierList) { i, not ->
                Item(i, earlierList.size, not,
                    { onClick(it) },
                    { onEmojiClick(it, not.first) })
                { onSwiped(it) }
            }
        }
        
        item { Spacer(Modifier.height(20.dp)) }
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
            item.first, item.second,
            lazyItemsShapes(index, size),
            getDifferenceOfTime(item.first.date)
        ), Modifier,
        { onClick(item.first) },
        { onEmojiClick(it) },
        { onSwiped(item.first) })
}

@Composable
private fun Label(text: Int) {
    Text(
        stringResource(text),
        Modifier.padding(vertical = 20.dp),
        colorScheme.tertiary,
        style = typography.labelLarge
    )
}