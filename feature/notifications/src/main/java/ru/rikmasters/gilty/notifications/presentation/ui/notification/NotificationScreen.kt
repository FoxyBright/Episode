package ru.rikmasters.gilty.notifications.presentation.ui.notification

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.meetbs.presentation.ui.Observe
import ru.rikmasters.gilty.meetbs.presentation.ui.ObserveType.MEET
import ru.rikmasters.gilty.meetbs.presentation.ui.ObserveType.USER
import ru.rikmasters.gilty.meetbs.viewmodel.ObserveViewModel
import ru.rikmasters.gilty.notifications.presentation.ui.responds.RespondsBs
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.RespondsBsViewModel
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
    
    val participantsStates by vm.participantsStates.collectAsState()
    val participants by vm.participants.collectAsState()
    
    val notifications by vm.notifications.collectAsState()
    val selected by vm.selectedNotification.collectAsState()
    val lastRespond by vm.lastRespond.collectAsState()
    val navState by vm.navBar.collectAsState()
    val ratings by vm.ratings.collectAsState()
    val blur by vm.blur.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getRatings()
        vm.getLastResponse()
        vm.getNotification()
    }
    NotificationsContent(
        NotificationsState(
            notifications, lastRespond,
            navState, blur, selected, participants,
            participantsStates, listState, ratings
        ), Modifier, object: NotificationsCallback {
            
            override fun onParticipantClick(index: Int) {
                scope.launch { vm.selectParticipants(index) }
            }
            
            override fun onBlurClick() {
                scope.launch {
                    vm.blur(false)
                    vm.clearSelectedNotification()
                }
            }
            
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
                                notification.parent.meeting?.id!!,
                                userId!!
                            )
                    } ?: run {
                        vm.selectNotification(notification)
                        vm.blur(true)
                    }
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
                        Connector<RespondsBsViewModel>(vm.scope) {
                            RespondsBs(it)
                        }
                    }
                }
            }
            
            override fun onListUpdate() {
                scope.launch { vm.getNotification() }
            }
            
            override fun onMeetClick(meet: MeetingModel) {
                scope.launch {
                    asm.bottomSheet.expand {
                        Connector<ObserveViewModel>(vm.scope) {
                            Observe(it, MEET, meet.id, meet.organizer?.id!!)
                        }
                    }
                }
            }
            
            override fun onUserClick(user: UserModel, meet: MeetingModel) {
                user.id?.let { u ->
                    scope.launch {
                        asm.bottomSheet.expand {
                            Connector<ObserveViewModel>(vm.scope) {
                                Observe(it, USER, meet.id, u)
                            }
                        }
                    }
                }
            }
            
            override fun onSwiped(notification: NotificationModel) {
                scope.launch { vm.swipeNotification(notification) }
            }
        })
}