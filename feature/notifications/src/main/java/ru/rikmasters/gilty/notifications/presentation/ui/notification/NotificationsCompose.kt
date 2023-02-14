package ru.rikmasters.gilty.notifications.presentation.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItem
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItemState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Responds
import ru.rikmasters.gilty.shared.common.extentions.getDifferenceOfTime
import ru.rikmasters.gilty.shared.common.extentions.rememberDragRowState
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationModelList
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.profile.DemoRatingModelList
import ru.rikmasters.gilty.shared.model.profile.RatingModel
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
                    ), Pair((3), ""), listOf(), (false),
                    DemoNotificationMeetingOverModel,
                    listOf(), listOf(), LazyListState(),
                    DemoRatingModelList
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
                    ), Pair((3), ""), listOf(), (true),
                    DemoNotificationMeetingOverModel,
                    listOf(), listOf(), LazyListState(),
                    DemoRatingModelList
                )
            )
        }
    }
}

data class NotificationsState(
    val notifications: Triple<List<NotificationModel>,
            List<NotificationModel>, List<NotificationModel>>,
    val lastRespond: Pair<Int, String>,
    val navBar: List<NavIconState>,
    val blur: Boolean,
    val activeNotification: NotificationModel?,
    val participants: List<UserModel>,
    val participantsStates: List<Int>,
    val listState: LazyListState,
    val ratings: List<RatingModel>,
)

interface NotificationsCallback {
    
    fun onSwiped(notification: NotificationModel)
    fun onEmojiClick(
        notification: NotificationModel,
        emoji: EmojiModel,
        userId: String?,
    )
    
    fun onRespondsClick()
    fun onBlurClick()
    fun onParticipantClick(index: Int)
    fun onNavBarSelect(point: Int)
    fun onListUpdate()
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
                state.navBar, Modifier
            ) { callback?.onNavBarSelect(it) }
        }
    ) { padding ->
        Notifications(
            state, Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            callback
        )
    }
    state.activeNotification?.let {
        if(state.blur) Box(
            Modifier
                .fillMaxSize() /*TODO заменить на блюр*/
                .background(colorScheme.background)
                .clickable { callback?.onBlurClick() }
        ) {
            ObserveNotification(
                ObserveNotificationState(
                    it, state.participants,
                    state.participantsStates,
                    (it.feedback?.ratings?.map { it.emoji }
                        ?: state.ratings.map { it.emoji })
                ), Modifier
                    .padding(horizontal = 16.dp)
                    .align(Center),
                callback
            )
        }
    }
}

@Composable
private fun Notifications(
    state: NotificationsState,
    modifier: Modifier = Modifier,
    callback: NotificationsCallback?,
) {
    val notifications =
        state.notifications
    val todayList =
        notifications.first.distinct().map {
            it to rememberDragRowState()
        }
    val weekList =
        notifications.second.distinct().map {
            it to rememberDragRowState()
        }
    val earlierList =
        notifications.third.distinct().map {
            it to rememberDragRowState()
        }
    LazyColumn(modifier, state.listState) {
        if(state.lastRespond.first != 0) item {
            Responds(
                stringResource(R.string.notification_responds_on_user_meetings),
                state.lastRespond.first,
                state.lastRespond.second,
                Modifier.padding(vertical = 12.dp)
            ) { callback?.onRespondsClick() }
        }
        
        if(todayList.isNotEmpty()) {
            item { Label(R.string.meeting_profile_bottom_today_label) }
            itemsIndexed(todayList, { _, it -> it },
                { _, it -> it.first.type }) { i, not ->
                NotificationItem(
                    NotificationItemState(
                        not.first, not.second,
                        lazyItemsShapes(i, todayList.size),
                        getDifferenceOfTime(not.first.date),
                        (not.first.feedback?.ratings?.map { it.emoji }
                            ?: state.ratings.map { it.emoji })
                    ), Modifier, callback
                )
            }
        }
        
        if(weekList.isNotEmpty()) {
            item { Label(R.string.notification_on_this_week_label) }
            itemsIndexed(weekList, { _, it -> it },
                { _, it -> it.first.type }) { i, not ->
                NotificationItem(
                    NotificationItemState(
                        not.first, not.second,
                        lazyItemsShapes(i, weekList.size),
                        getDifferenceOfTime(not.first.date),
                        (not.first.feedback?.ratings?.map { it.emoji }
                            ?: state.ratings.map { it.emoji })
                    ), Modifier, callback
                )
            }
        }
        
        if(earlierList.isNotEmpty()) {
            item { Label(R.string.notification_earlier_label) }
            itemsIndexed(earlierList, { _, it -> it },
                { _, it -> it.first.type }) { i, not ->
                NotificationItem(
                    NotificationItemState(
                        not.first, not.second,
                        lazyItemsShapes(i, earlierList.size),
                        getDifferenceOfTime(not.first.date),
                        (not.first.feedback?.ratings?.map { it.emoji }
                            ?: state.ratings.map { it.emoji })
                    ), Modifier, callback
                )
            }
        }
        
        item {
            if(notifications.first.isNotEmpty()
                || notifications.second.isNotEmpty()
                || notifications.third.isNotEmpty()
            ) callback?.onListUpdate()
            
        }
        
        item { Spacer(Modifier.height(20.dp)) }
    }
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