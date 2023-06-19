package ru.rikmasters.gilty.notifications.presentation.ui.notification

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.paging.LoadState.NotLoading
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.notifications.presentation.ui.notification.NotificationPeriodNames.Companion.getPeriodName
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItem
import ru.rikmasters.gilty.notifications.presentation.ui.notification.item.NotificationItemState
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.BackBlur
import ru.rikmasters.gilty.shared.common.Responds
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.LastRespond
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel.DEFAULT
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
                    notifications = pagingPreview(
                        DemoNotificationModelList
                    ),
                    splitNotifications = listOf(),
                    lastRespond = LastRespond(
                        image = "",
                        isOnline = false,
                        group = DEFAULT,
                        count = 0
                    ),
                    navBar = listOf(),
                    blur = false,
                    activeNotification = DemoNotificationMeetingOverModel,
                    participants = pagingPreview(DemoUserModelList),
                    participantsStates = listOf(),
                    listState = LazyListState(),
                    ratings = DemoRatingModelList
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
                    notifications = pagingPreview(
                        DemoNotificationModelList
                    ),
                    splitNotifications = listOf(),
                    lastRespond = LastRespond(
                        image = "",
                        isOnline = false,
                        group = DEFAULT,
                        count = 0
                    ),
                    navBar = listOf(),
                    blur = true,
                    activeNotification = DemoNotificationMeetingOverModel,
                    participants = pagingPreview(DemoUserModelList),
                    participantsStates = listOf(),
                    listState = LazyListState(),
                    ratings = DemoRatingModelList
                )
            )
        }
    }
}

data class NotificationsState(
    val notifications: LazyPagingItems<NotificationModel>,
    val splitNotifications: List<Triple<Int, Boolean, NotificationModel>>,
    val lastRespond: LastRespond,
    val navBar: List<NavIconState>,
    val blur: Boolean,
    val activeNotification: NotificationModel?,
    val participants: LazyPagingItems<UserModel>,
    val participantsStates: List<Int>,
    val listState: LazyListState,
    val ratings: List<RatingModel>,
    val smthError: Boolean = false,
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
                text = stringResource(
                    R.string.notification_screen_name
                ),
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 80.dp,
                    bottom = 10.dp
                ),
                style = typography.titleLarge
                    .copy(colorScheme.tertiary)
            )
        },
        bottomBar = {
            NavBar(
                state = state.navBar,
                modifier = Modifier
            ) { callback?.onNavBarSelect(it) }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            Use<NotificationViewModel>(PullToRefreshTrait) {
                Notifications(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    callback = callback
                )
            }
        }
    }

    state.activeNotification?.let {
        if (state.blur) BackBlur(
            Modifier.clickable {
                callback?.onBlurClick()
            },
            radius = 25,
        ) {
            ObserveNotification(
                state = ObserveNotificationState(
                    notification = it,
                    participants = state.participants,
                    participantsStates = state.participantsStates,
                    notificationEmojiList =
                    it.feedback?.ratings?.map { it.emoji }
                        ?: state.ratings.map { it.emoji },
                    emojiList = state.ratings.map { it.emoji }),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 84.dp)
                    .align(TopCenter),
                callback = callback
            )
        }
    }
}

private fun LazyListScope.emptyNotification(
    modifier: Modifier = Modifier,
) {
    item {
        Column(
            modifier
                .fillMaxSize()
                .padding(top = 56.dp),
            Top, CenterHorizontally
        ) {
            Image(
                painter = painterResource(
                    if (isSystemInDarkTheme())
                        R.drawable.notify_dog_dark
                    else R.drawable.notify_dog_light
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(
                    R.string.notification_place_holder
                ),
                modifier = Modifier
                    .padding(top = 26.dp),
                style = typography.bodyMedium.copy(
                    color = colorScheme.scrim,
                    fontWeight = SemiBold
                )
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
    val defEmoji = remember(state.ratings) {
        state.ratings.map { it.emoji }
    }
    LazyColumn(modifier, state.listState) {
        val load = state.notifications.loadState
        when {
            load.refresh is Error -> Unit
            load.append is Error -> Unit
            else -> content(
                load = load,
                hasResponds = state.lastRespond.count > 0,
                notifications = state.notifications,
                splitNotifications = state.splitNotifications,
                state = state,
                defEmoji = defEmoji,
                callback = callback
            )
        }
        itemSpacer(20.dp)
    }
}

private fun LazyListScope.content(
    load: CombinedLoadStates,
    hasResponds: Boolean,
    notifications: LazyPagingItems<NotificationModel>,
    splitNotifications: List<Triple<Int, Boolean, NotificationModel>>,
    state: NotificationsState,
    defEmoji: List<EmojiModel>,
    callback: NotificationsCallback?,
) {
    if (load.refresh is Loading) loader(load)
    if (hasResponds) responds(state.lastRespond, callback)
    if (
        notifications.itemCount > 0
        && splitNotifications.isNotEmpty()
    ) {
        notificationsList(
            state = state,
            defEmoji = defEmoji,
            splitNotifications = splitNotifications,
            hasResponds = hasResponds,
            callback = callback
        )
        if (load.append is Loading
            || splitNotifications.size
            != state.notifications.itemCount
        ) loader(load)
    } else if (load.refresh is NotLoading)
        emptyNotification()
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.notificationsList(
    state: NotificationsState,
    defEmoji: List<EmojiModel>,
    splitNotifications: List<Triple<Int, Boolean, NotificationModel>>,
    hasResponds: Boolean,
    callback: NotificationsCallback?,
) {
    val items = state.notifications
    /*itemsIndexed(
        items = items,
        key = { index, item ->
             item.id
              },
        *//*contentType = { index, item ->
            (item)?.type ?: ADMIN_NOTIFICATION

        }*//*

    ) { index, item ->
        item?.let {
            ElementNot(
                index = doesPrevExist(index, splitNotifications).toInt(),
                size = getSize(
                    prev = doesPrevExist(index, splitNotifications),
                    next = doesNextExist(index, splitNotifications),
                ),
                item = item,
                ratings = defEmoji,
                modifier = Modifier.animateItemPlacement(),
                callback = callback
            )
        }
    }*/
    itemsIndexed(
        items = items,
        key = { index, item ->
            index
     /*       if (splitNotifications.size > index) {
                val splitNotItem = splitNotifications[index]
                if (!splitNotItem.second) {
                    item.id
                } else null
            } else null*/
        },
        /*contentType = { index, item ->
            (item)?.type ?: ADMIN_NOTIFICATION

        }*/

    ) { index, item ->
        if (splitNotifications.size > index) {
            val splitNotItem = splitNotifications[index]
            if (!splitNotItem.second) {
                // Displays Labels
                if (
                    index == 0
                    || splitNotifications[index - 1].first
                    != splitNotItem.first
                ) Label(
                    text = getPeriodName(splitNotItem.first),
                    hasResponds = hasResponds,
                    isFirst = !doesPrevExist(index, splitNotifications)
                )
                // Displays actual notification
                Text(text = "$index", fontSize = 22.sp)
                ElementNot(
                    index = doesPrevExist(index, splitNotifications).toInt(),
                    size = getSize(
                        prev = doesPrevExist(index, splitNotifications),
                        next = doesNextExist(index, splitNotifications),
                    ),
                    item = splitNotItem.third,
                    ratings = defEmoji,
                    modifier = Modifier.animateItemPlacement(),
                    callback = callback
                )
            }
        }
        /*if(splitNotifications.size > index) {
            // Displays Labels
            if(
                index == 0
                || splitNotifications[index - 1].first
                != splitNotifications[index].first
            ) Label(
                text = getPeriodName(splitNotifications[index].first),
                hasResponds = hasResponds,
                isFirst = !doesPrevExist(index, splitNotifications)
            )
            // Displays actual notification
            ElementNot(
                index = doesPrevExist(index, splitNotifications).toInt(),
                size = getSize(
                    prev = doesPrevExist(index, splitNotifications),
                    next = doesNextExist(index, splitNotifications),
                ),
                item = splitNotifications[index].second,
                ratings = defEmoji,
                modifier = Modifier.animateItemPlacement(),
                callback = callback
            )
        }*/
    }
}

private fun LazyListScope.responds(
    lastRespond: LastRespond,
    callback: NotificationsCallback?,
) {
    item {
        Box(Modifier.padding(top = 20.dp)) {
            Responds(lastRespond) {
                callback?.onRespondsClick()
            }
        }
    }
}

private fun LazyListScope.loader(
    load: CombinedLoadStates,
) {
    item { PagingLoader(load) }
}

private fun doesPrevExist(
    index: Int,
    splitNotifications: List<Triple<Int, Boolean, NotificationModel>>,
) = if (index == 0) false else try {
    splitNotifications[index - 1].first ==
            splitNotifications[index].first
} catch (e: Exception) {
    false
}

private fun doesNextExist(
    index: Int,
    splitNotifications: List<Triple<Int, Boolean, NotificationModel>>,
) = if (index + 1 > splitNotifications.size) false else try {
    splitNotifications[index + 1].first ==
            splitNotifications[index].first
} catch (e: Exception) {
    false
}

private fun getSize(
    prev: Boolean,
    next: Boolean,
) = when {
    !prev && !next -> 1
    prev && next -> 3
    else -> 2
}

@Composable
private fun ElementNot(
    index: Int,
    size: Int,
    item: NotificationModel,
    ratings: List<EmojiModel>,
    modifier: Modifier,
    callback: NotificationsCallback?,
) {
    (item to rememberDragRowState())
        .let { (not, row) ->
            NotificationItem(
                state = NotificationItemState(
                    notification = not,
                    rowState = row,
                    shape = lazyItemsShapes(index, size),
                    duration = getDifferenceOfTime(not.date),
                    emojiList = not getEmoji ratings
                ),
                modifier = modifier,
                callback = callback
            )
        }
}

private infix fun NotificationModel.getEmoji(
    defEmoji: List<EmojiModel>,
) = feedback?.ratings?.map { it.emoji } ?: defEmoji

@Composable
private fun Label(
    text: Int,
    hasResponds: Boolean,
    isFirst: Boolean,
) {
    Text(
        text = stringResource(text),
        modifier = Modifier.padding(
            top = when {
                hasResponds -> 24
                isFirst -> 20
                else -> 28
            }.dp,
            bottom = 18.dp
        ),
        style = typography.labelLarge
            .copy(colorScheme.tertiary)
    )
}