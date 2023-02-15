package ru.rikmasters.gilty.notifications.presentation.ui.notification

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.notifications.presentation.ui.bottoms.meets.ObserveBs
import ru.rikmasters.gilty.notifications.presentation.ui.bottoms.responds.RespondsBs
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.ObserveBsViewModel
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.RespondsBsViewModel
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation.MEET
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation.ORGANIZER
import ru.rikmasters.gilty.shared.image.EmojiModel
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
    
    Use<NotificationViewModel>(LoadingTrait) {
        NotificationsContent(
            NotificationsState(
                vm.sortNotification(notifications),
                lastRespond, navState, blur,
                selected, participants,
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
                    scope.launch {
                        vm.getNotification()
                        if(notifications.size > 6) listState.scrollToItem(
                            notifications.size - 6
                        )
                    }
                }
                
                override fun onMeetClick(meet: MeetingModel?) {
                    meet?.let {
                        scope.launch {
                            asm.bottomSheet.expand {
                                Connector<ObserveBsViewModel>(vm.scope) {
                                    ObserveBs(it, MEET, meet)
                                }
                            }
                        }
                    }
                }
                
                override fun onUserClick(user: UserModel?) {
                    user?.id?.let {
                        scope.launch {
                            asm.bottomSheet.expand {
                                Connector<ObserveBsViewModel>(vm.scope) {
                                    ObserveBs(it, ORGANIZER, (null), user)
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
}