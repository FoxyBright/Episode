package ru.rikmasters.gilty.notifications.presentation.ui.notification

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
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

@Composable
fun NotificationsScreen(vm: NotificationViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val memberList by remember { mutableStateOf(listOf(DemoMemberModel, DemoMemberModel)) }
    val memberListWrap = remember { mutableStateListOf<Boolean>() }
    
    val notifications by vm.notifications.collectAsState()
    val selected by vm.selectedNotify.collectAsState()
    val lastRespond by vm.lastRespond.collectAsState()
    val navState by vm.navBar.collectAsState()
    val blur by vm.blur.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getLastResponse()
        vm.getNotification()
    }
    
    Use<NotificationViewModel>(LoadingTrait) {
        NotificationsContent(
            NotificationsState(
                vm.sortNotification(notifications),
                lastRespond, navState, blur,
                selected, memberList, memberListWrap
            ), Modifier, object: NotificationsCallback {
                
                override fun onClick(notification: NotificationModel) {
                    scope.launch {
                        vm.selectNotification(notification)
                        vm.blur(true)
                    }
                }
                
                override fun onParticipantClick(index: Int) {
                    memberListWrap[index] = !memberListWrap[index]
                }
                
                override fun onBlurClick() {
                    scope.launch { vm.blur(false) }
                }
                
                override fun onEmojiClick(
                    emoji: EmojiModel,
                    notification: NotificationModel,
                ) {
                    scope.launch { vm.emojiClick(emoji, notification) }
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
                
                override fun onSwiped(notification: NotificationModel) {
                    scope.launch { vm.swipeNotification(notification) }
                }
            })
    }
}