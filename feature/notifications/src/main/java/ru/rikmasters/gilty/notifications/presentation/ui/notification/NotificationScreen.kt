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
import ru.rikmasters.gilty.notifications.presentation.ui.bottoms.responds.RespondsBs
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.RespondsBsViewModel
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

@Composable
fun NotificationsScreen(vm: NotificationViewModel) {
    
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val participants by vm.participants.collectAsState()
    val participantsStates by vm.participantsStates.collectAsState()
    
    val notifications by vm.notifications.collectAsState()
    val ratings by vm.ratings.collectAsState()
    val selected by vm.selectedNotification.collectAsState()
    val lastRespond by vm.lastRespond.collectAsState()
    val navState by vm.navBar.collectAsState()
    val blur by vm.blur.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getNotification()
        vm.getLastResponse()
        vm.getRatings()
    }
    
    Use<NotificationViewModel>(LoadingTrait) {
        NotificationsContent(
            NotificationsState(
                vm.sortNotification(notifications),
                lastRespond, navState, blur,
                selected, participants,
                participantsStates, listState, ratings
            ), Modifier, object: NotificationsCallback {
                
                override fun onClick(notification: NotificationModel) {
                    scope.launch {
                        vm.selectNotification(notification)
                        vm.getRatings()
                        vm.blur(true)
                    }
                }
                
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
                    emoji: EmojiModel, meetId: String, userId: String,
                ) {
                    scope.launch { vm.emojiClick(emoji, meetId, userId) }
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
                
                override fun onSwiped(notification: NotificationModel) {
                    scope.launch { vm.swipeNotification(notification) }
                }
            })
    }
}