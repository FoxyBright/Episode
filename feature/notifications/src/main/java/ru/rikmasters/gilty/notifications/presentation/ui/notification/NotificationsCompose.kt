package ru.rikmasters.gilty.notifications.presentation.ui.notification

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
import androidx.paging.LoadState.Loading
import androidx.paging.LoadState.NotLoading
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItem
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItemState
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.BackBlur
import ru.rikmasters.gilty.shared.common.Responds
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.DemoUserModelList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationMeetingOverModel
import ru.rikmasters.gilty.shared.model.notification.DemoNotificationModelList
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.profile.DemoRatingModelList
import ru.rikmasters.gilty.shared.model.profile.RatingModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import java.util.*

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
                    pagingPreview(DemoNotificationModelList),
                    Pair((3), ""), listOf(), (false),
                    DemoNotificationMeetingOverModel,
                    pagingPreview(DemoUserModelList),
                    listOf(), LazyListState(),
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
                    pagingPreview(DemoNotificationModelList),
                    Pair((3), ""), listOf(), (true),
                    DemoNotificationMeetingOverModel,
                    pagingPreview(DemoUserModelList),
                    listOf(), LazyListState(),
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
    val participants: LazyPagingItems<UserModel>,
    val participantsStates: List<Int>,
    val listState: LazyListState,
    val ratings: List<RatingModel>,
)

interface NotificationsCallback {
    
    fun onSwiped(notification: NotificationModel)
    fun onMeetClick(meet: MeetingModel?)
    fun onUserClick(
        user: UserModel?,
        meet: MeetingModel? = null,
    )
    
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
                ), colorScheme.tertiary,
                style = typography.titleLarge
            )
        }, bottomBar = {
            NavBar(
                state.navBar,
                Modifier
            ) { callback?.onNavBarSelect(it) }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            Use<NotificationViewModel>(PullToRefreshTrait) {
                Notifications(
                    state, Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    callback
                )
            }
        }
    }
    state.activeNotification?.let {
        if(state.blur) BackBlur(
            Modifier.clickable {
                callback?.onBlurClick()
            }, radius = 25
        ) {
            ObserveNotification(
                ObserveNotificationState(
                    it,
                    state.participants,
                    state.participantsStates,
                    (it.feedback?.ratings?.map {
                        it.emoji
                    } ?: state.ratings.map { it.emoji })
                ), Modifier
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
            .padding(top = 56.dp),
        Top, CenterHorizontally
    ) {
        Image(
            painterResource(
                if((isSystemInDarkTheme())) {
                    R.drawable.notify_dog_dark
                } else R.drawable.notify_dog_light
            ), (null), Modifier.fillMaxWidth()
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
    @Composable
    fun getMonthName(monthNumber:Int) = when(monthNumber){
            1->R.string.month_january_name
            2->R.string.month_february_name
            3->R.string.month_march_name
            4->R.string.month_april_name
            5->R.string.month_may_name
            6->R.string.month_june_name
            7->R.string.month_july_name
            8->R.string.month_august_name
            9->R.string.month_september_name
            10->R.string.month_october_name
            11->R.string.month_november_name
            12->R.string.month_december_name
            else->R.string.month_december_name
        }
    val notifications = state.notifications
    if(LocalInspectionMode.current) PreviewLazy()
    else LazyColumn(modifier, state.listState) {
        
        val hasResponds = state.lastRespond.first > 0
        val firstItem by lazy {
            state.notifications.itemSnapshotList.first()
        }
        
        when {
            notifications.loadState.refresh is LoadState.Error -> {}
            notifications.loadState.append is LoadState.Error -> {}
            else -> {
                if(notifications.loadState.refresh is Loading) {
                    item { PagingLoader(notifications.loadState) }
                }
                
                val last = state.lastRespond
                if(hasResponds) item {
                    Box(Modifier.padding(top = 20.dp)) {
                        Responds(
                            last.second, last.first
                        ) { callback?.onRespondsClick() }
                    }
                }
                val itemCount = notifications.itemCount
                if(itemCount > 0) {
                    val todayItems =
                        notifications.itemSnapshotList.items.filter {
                            todayControl(it.date)
                        }
                    
                    if(todayItems.isNotEmpty()) {
                        item {
                            Label(
                                R.string.meeting_profile_bottom_today_label,
                                hasResponds,
                                (todayItems.first() == firstItem)
                            )
                        }
                        itemsIndexed(todayItems) { count, item ->
                            ElementNot(
                                count, todayItems.size,
                                item, state.ratings,
                                callback
                            )
                        }
                    }
                    
                    val weekItems =
                        notifications.itemSnapshotList.items.filter {
                            weekControl(it.date) && !todayControl(it.date)
                        }
                    
                    if(weekItems.isNotEmpty()) {
                        item {
                            Label(
                                R.string.notification_on_this_week_label,
                                hasResponds,
                                (weekItems.first() == firstItem)
                            )
                        }
                        itemsIndexed(weekItems) { count, item ->
                            ElementNot(
                                count, weekItems.size,
                                item, state.ratings, callback
                            )
                        }
                    }
                    
                    val restItems =
                        notifications.itemSnapshotList.items.filter {
                            earlierWeekControl(it.date)
                        }
                    
                    if(restItems.isNotEmpty()) {
                        itemsIndexed(state.notifications) { index, item ->
                            item?.let { not ->
                                if(earlierWeekControl(not.date)) {
                                    // First Element
                                    if(index == todayItems.size + weekItems.size){
                                        Label(
                                            getMonthName(monthNumber = getMonth(item.date)),
                                            hasResponds,
                                            if(weekItems.isNotEmpty()) (weekItems.first() == firstItem) else (true)
                                        )
                                        ElementNot(
                                            0,
                                            restItems.filter { getMonth(it.date) == getMonth(item.date) }.size, not,
                                            state.ratings, callback
                                        )
                                    }else if(index - 1 >= 0) { // other elemnets
                                        if(getMonth(item.date) != getMonth(state.notifications.itemSnapshotList[index - 1]?.date?:"")){
                                            Label(
                                                getMonthName(monthNumber = getMonth(item.date)),
                                                hasResponds,
                                                if(weekItems.isNotEmpty()) (weekItems.first() == firstItem) else (false)
                                            )
                                        }
                                        ElementNot(
                                            index - todayItems.size - weekItems.size - restItems.filter
                                            { isAfter(it.date,item.date) && getMonth(it.date) != getMonth(item.date) }.size,
                                            restItems.filter { getMonth(it.date) == getMonth(item.date) }.size, not,
                                            state.ratings, callback
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    if(notifications.loadState.append is Loading)
                        item { PagingLoader(notifications.loadState) }
                } else if(notifications.loadState.refresh is NotLoading)
                    item { EmptyNotification() }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun PreviewLazy() {
    LazyColumn(
        Modifier.padding(horizontal = 16.dp)
    ) {
        item {
            Label(
                R.string.notification_earlier_label,
                isFirst = true,
                hasResponds = false
            )
        }
        itemsIndexed(
            DemoNotificationModelList
        ) { it, not ->
            ElementNot(
                it, (2), not,
                DemoRatingModelList
            )
        }
    }
}

@Composable
private fun ElementNot(
    index: Int, size: Int,
    item: NotificationModel,
    ratings: List<RatingModel>,
    callback: NotificationsCallback? = null,
) {
    (item to rememberDragRowState())
        .let { (not, row) ->
            NotificationItem(
                NotificationItemState(
                    not, row, lazyItemsShapes(index, size),
                    getDifferenceOfTime(not.date),
                    (not.feedback?.ratings?.map { it.emoji }
                        ?: ratings.map { it.emoji })
                ), Modifier, callback
            )
        }
}

@Composable
private fun Label(
    text: Int,
    hasResponds: Boolean,
    isFirst: Boolean,
) {
    Text(
        stringResource(text),
        Modifier.padding(
            top = when {
                hasResponds -> 24
                isFirst -> 20
                else -> 28
            }.dp, bottom = 18.dp
        ), colorScheme.tertiary,
        style = typography.labelLarge
    )
}
