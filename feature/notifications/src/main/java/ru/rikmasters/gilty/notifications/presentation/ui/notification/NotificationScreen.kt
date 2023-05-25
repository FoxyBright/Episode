package ru.rikmasters.gilty.notifications.presentation.ui.notification

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.RESPONDS
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.USER
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.internetCheck
import ru.rikmasters.gilty.core.data.source.SharedPrefListener.Companion.listenPreference
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.shared.common.extentions.rememberLazyListScrollState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NotificationsScreen(vm: NotificationViewModel) {
    
    val listState = rememberLazyListScrollState("notifications")
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val splitNotifications by vm.splitMonthNotifications.collectAsState()
    val notifications = vm.notifications.collectAsLazyPagingItems()
    val participants = vm.participants.collectAsLazyPagingItems()
    val participantsStates by vm.participantsStates.collectAsState()
    val selected by vm.selectedNotification.collectAsState()
    val lastRespond by vm.lastRespond.collectAsState()
    val ratings by vm.ratings.collectAsState()
    val blur by vm.blur.collectAsState()
    
    val unreadMessages by vm.unreadMessages.collectAsState()
    val navBar = remember {
        mutableListOf(
            INACTIVE, ACTIVE, INACTIVE,
            unreadMessages, INACTIVE
        )
    }
    
    LaunchedEffect(Unit) {
        vm.getRatings()
        vm.getLastResponse()
        context.listenPreference(
            key = "unread_messages",
            defValue = 0
        ) {
            scope.launch {
                vm.setUnreadMessages(it > 0)
            }
        }
    }
    
    LaunchedEffect(
        notifications.itemSnapshotList.items
    ) {
        scope.launch {
            vm.splitByMonthSM(
                notifications.itemSnapshotList.items
            )
        }
    }
    
    var errorState by remember {
        mutableStateOf(false)
    }
    
    scope.launch {
        while(true) {
            delay(500)
            internetCheck(context).let {
                if(!it) errorState = true
            }
        }
    }
    
    NotificationsContent(
        state = NotificationsState(
            notifications = notifications,
            splitNotifications = splitNotifications,
            lastRespond = lastRespond,
            navBar = navBar,
            blur = blur,
            activeNotification = selected,
            participants = participants,
            participantsStates = participantsStates,
            listState = listState,
            ratings = ratings,
            smthError = errorState
        ),
        callback = object: NotificationsCallback {
            
            override fun onEmojiClick(
                notification: NotificationModel,
                emoji: EmojiModel,
                userId: String?,
            ) {
                scope.launch {
                    selected?.let {
                        if(notification.feedback?.ratings == null || !userId.isNullOrBlank()) {
                            vm.emojiClick(
                                emoji,
                                notification.parent.meeting?.id ?: "",
                                userId ?: "",
                                notification.parent.meeting?.organizer?.id.equals(
                                    userId
                                )
                            )
                            // Updates ratings
                            vm.forceRefresh()
                            vm.forceRefreshMembers()
                        }
                    } ?: run {
                        vm.selectNotification(notification)
                        vm.blur(true)
                    }
                }
            }
            
            override fun onMeetClick(meet: MeetingModel?) {
                scope.launch {
                    meet?.let { m ->
                        asm.bottomSheet.expand {
                            BottomSheet(vm.scope, MEET, m.id)
                        }
                    }
                }
            }
            
            override fun onUserClick(
                user: UserModel?,
                meet: MeetingModel?,
            ) {
                scope.launch {
                    user?.id?.let { u ->
                        asm.bottomSheet.expand {
                            BottomSheet(vm.scope, USER, meet?.id, u)
                        }
                    }
                }
            }
            
            override fun onBlurClick() {
                scope.launch {
                    vm.blur(false)
                    vm.clearSelectedNotification()
                }
            }
            
            override fun onNavBarSelect(point: Int) {
                if(point == 1) return
                scope.launch {
                    nav.navigateAbsolute(
                        vm.navBarNavigate(point)
                    )
                }
            }
            
            override fun onRespondsClick() {
                scope.launch {
                    asm.bottomSheet.expand {
                        BottomSheet(vm.scope, RESPONDS)
                    }
                }
            }
            
            override fun onSwiped(notification: NotificationModel) {
                scope.launch { vm.swipeNotification(notification) }
            }
            
            override fun onParticipantClick(index: Int) {
                scope.launch { vm.selectParticipants(index) }
            }
            
            override fun onListUpdate() {
                errorState = !internetCheck(context)
                if(!errorState) scope.launch {
                    vm.forceRefresh()
                }
            }
        }
    )
}
