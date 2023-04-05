package ru.rikmasters.gilty.notifications.presentation.ui.notification

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.paging.LoadState.Loading
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.RESPONDS
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.USER
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

@Composable
fun NotificationsScreen(vm: NotificationViewModel) {
    
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val notifications = vm.notifications.collectAsLazyPagingItems()
    val participantsStates by vm.participantsStates.collectAsState()
    val selected by vm.selectedNotification.collectAsState()
    val participants by vm.participants.collectAsState()
    val lastRespond by vm.lastRespond.collectAsState()
    val navState by vm.navBar.collectAsState()
    val ratings by vm.ratings.collectAsState()
    val blur by vm.blur.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getChatStatus()
        vm.getRatings()
        vm.getLastResponse()
    }
    
    LaunchedEffect(notifications.loadState) {
        val loadState = notifications.loadState
        vm.isPageRefreshing.emit(
            loadState.append is Loading
                    || loadState.refresh is Loading
        )
    }
    
    fun refresh() = scope.launch {
        vm.forceRefresh()
        notifications.refresh()
    }
    
    NotificationsContent(
        NotificationsState(
            notifications, lastRespond,
            navState, blur, selected,
            participants, participantsStates,
            listState, ratings
        ), Modifier, object: NotificationsCallback {
            
            override fun onEmojiClick(
                notification: NotificationModel,
                emoji: EmojiModel,
                userId: String?,
            ) {
                scope.launch {
                    selected?.let {
                        if(notification.feedback?.ratings == null)
                            vm.emojiClick(
                                emoji,
                                notification.parent.meeting?.id ?: "",
                                userId ?: ""
                            )
                        refresh()
                    } ?: run {
                        vm.selectNotification(notification)
                        vm.blur(true)
                    }
                }
            }
            
            override fun onMeetClick(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        BottomSheet(vm.scope, MEET, meet.id)
                    }
                }
            }
            
            override fun onUserClick(user: UserModel, meet: MeetingModel) {
                scope.launch {
                    user.id?.let { u ->
                        asm.bottomSheet.expand {
                            BottomSheet(vm.scope, USER, meet.id, u)
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
                scope.launch { refresh() }
            }
        }
    )
}