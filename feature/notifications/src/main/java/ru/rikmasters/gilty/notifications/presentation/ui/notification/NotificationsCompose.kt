package ru.rikmasters.gilty.notifications.presentation.ui.notification

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import kotlinx.coroutines.flow.flowOf
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItem
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItemState
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.Responds
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationModelList
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.profile.DemoRatingModelList
import ru.rikmasters.gilty.shared.model.profile.RatingModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
private fun previewData() = flowOf(
    PagingData.from(DemoNotificationModelList)
).collectAsLazyPagingItems()

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
                    previewData(),
                    Pair((3), ""), listOf(), (false),
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
                    previewData(),
                    Pair((3), ""), listOf(), (true),
                    DemoNotificationMeetingOverModel,
                    listOf(), listOf(), LazyListState(),
                    DemoRatingModelList
                )
            )
        }
    }
}

data class NotificationsState(
    val notifications: LazyPagingItems<NotificationModel>,
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
    fun onMeetClick(meet: MeetingModel)
    fun onUserClick(user: UserModel, meet: MeetingModel)
    fun onRespondsClick()
    fun onBlurClick()
    fun onParticipantClick(index: Int)
    fun onNavBarSelect(point: Int)
    fun onListUpdate()
    fun onEmojiClick(
        notification: NotificationModel,
        emoji: EmojiModel,
        userId: String?,
    )
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
                ),
                colorScheme.tertiary,
                style = typography.titleLarge
            )
        },
        bottomBar = {
            NavBar(
                state.navBar,
                Modifier
            ) { callback?.onNavBarSelect(it) }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            Use<NotificationViewModel>(PullToRefreshTrait) {
                Notifications(
                    state,
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    callback
                )
            }
        }
    }
    state.activeNotification?.let {
        if(state.blur) Box(
            Modifier
                .fillMaxSize() /*TODO заменить на блюр, по неизвестным причинам BlurBox() вылетает*/
                .background(colorScheme.background)
                .clickable { callback?.onBlurClick() }
        ) {
            ObserveNotification(
                ObserveNotificationState(
                    it,
                    state.participants,
                    state.participantsStates,
                    (
                            it.feedback?.ratings?.map { it.emoji }
                                ?: state.ratings.map { it.emoji }
                            )
                ),
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 84.dp)
                    .align(TopCenter),
                callback
            )
        }
    }
}

@Composable
private fun EmptyNotification(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        Top,
        CenterHorizontally
    ) {
        Image(
            painterResource(
                if((isSystemInDarkTheme())) {
                    R.drawable.notify_dog_dark
                } else R.drawable.notify_dog_light
            ),
            (null),
            Modifier.fillMaxWidth()
        )
        Text(
            stringResource(R.string.notification_place_holder),
            Modifier.padding(top = 26.dp),
            color = colorScheme.scrim,
            style = typography.bodyMedium.copy(
                fontWeight = SemiBold
            )
        )
    }
}

@Composable
private fun Notifications(
    state: NotificationsState,
    modifier: Modifier = Modifier,
    callback: NotificationsCallback?,
) {
    val notifications = state.notifications
    val itemCount = notifications.itemCount
    
    if(LocalInspectionMode.current) PreviewLazy()
    else LazyColumn(modifier, state.listState) {
        
        when {
            notifications.loadState.refresh is LoadState.Error -> {}
            notifications.loadState.append is LoadState.Error -> {}
            else -> {
                if(notifications.loadState.refresh is LoadState.Loading) {
                    item { PagingLoader(notifications.loadState) }
                }
                
                val last = state.lastRespond
                if(last.first > 0) item {
                    Box(Modifier.padding(vertical = 22.dp)) {
                        Responds(
                            last.second,
                            last.first
                        ) { callback?.onRespondsClick() }
                    }
                }
                
                if(itemCount > 0) {
                    val todayItems = notifications.itemSnapshotList.items.filter {
                        todayControl(it.date)
                    }
                    if(todayItems.isNotEmpty()) {
                        item { Label(R.string.meeting_profile_bottom_today_label) }
                        itemsIndexed(state.notifications) { count, item ->
                            item?.let {
                                if (todayControl(item.date)) {
                                    ElementNot(
                                        count,
                                        todayItems.size,
                                        item,
                                        state.ratings,
                                        callback
                                    )
                                }
                            }
                        }
                    }
                    
                    val weekItems = notifications.itemSnapshotList.items.filter {
                        weekControl(it.date)
                    }
                    
                    if(weekItems.isNotEmpty()) {
                        item { Label(R.string.notification_on_this_week_label) }
                        itemsIndexed(state.notifications) { count, item ->
                            item?.let {
                                if (weekControl(item.date)) {
                                    ElementNot(
                                        count,
                                        todayItems.size,
                                        item,
                                        state.ratings,
                                        callback
                                    )
                                }
                            }
                        }
                    }
                    
                    val restItems = notifications.itemSnapshotList.items.filter {
                        earlierWeekControl(it.date)
                    }
                    
                    if(restItems.isNotEmpty()) {
                        item { Label(R.string.notification_earlier_label) }
                        itemsIndexed(state.notifications) { count, item ->
                            item?.let {
                                if ( earlierWeekControl(item.date)) {
                                    ElementNot(
                                        count,
                                        todayItems.size,
                                        item,
                                        state.ratings,
                                        callback
                                    )
                                }
                            }
                        }
                    }

                    if(notifications.loadState.append is LoadState.Loading) {
                        item { PagingLoader(notifications.loadState) }
                    }
                } else {
                    if(notifications.loadState.refresh is LoadState.NotLoading) {
                        item { EmptyNotification() }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun PreviewLazy() {
    LazyColumn(Modifier.padding(horizontal = 16.dp)) {
        item { Label(R.string.notification_earlier_label) }
        itemsIndexed(DemoNotificationModelList) { it, not ->
            ElementNot(it, (2), not, DemoRatingModelList)
        }
    }
}

@Composable
private fun ElementNot(
    index: Int,
    size: Int,
    item: NotificationModel,
    ratings: List<RatingModel>,
    callback: NotificationsCallback? = null,
) {
    val not = item to rememberDragRowState()
    NotificationItem(
        NotificationItemState(
            not.first,
            not.second,
            lazyItemsShapes(index, size),
            getDifferenceOfTime(not.first.date),
            (
                    not.first.feedback?.ratings?.map { it.emoji }
                        ?: ratings.map { it.emoji }
                    )
        ),
        Modifier,
        callback
    )
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
