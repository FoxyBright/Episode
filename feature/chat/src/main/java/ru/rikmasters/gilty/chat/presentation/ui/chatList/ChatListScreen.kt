package ru.rikmasters.gilty.chat.presentation.ui.chatList

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.CONFIRM
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chat.presentation.ui.viewmodel.ChatListViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.shared.model.chat.ChatModel

@Composable
fun ChatListScreen(vm: ChatListViewModel) {
    
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val nav = get<NavState>()
    
    val unreadDialogs by vm.unreadDialogs.collectAsState()
    val chatToDelete by vm.chatToDelete.collectAsState()
    val alertSelected by vm.alertSelected.collectAsState()
    val alertState by vm.alertState.collectAsState()
    val dialogs by vm.dialogs.collectAsState()
    val navBar by vm.navBar.collectAsState()
    val completed by vm.completed.collectAsState()
    val profile by vm.profile.collectAsState()
    val unRead by vm.unread.collectAsState()
    val alert by vm.alert.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getDialogs()
        vm.getProfile()
    }
    
    Use<ChatListViewModel>(LoadingTrait) {
        ChatListContent(
            ChatListState(
                navBar, dialogs,
                completed, alert, alertState,
                alertSelected, unRead,
                listState, unreadDialogs
            ), Modifier, object: ChatListCallback {
                
                override fun onNavBarSelect(point: Int) {
                    if(point == 3) return
                    scope.launch {
                        nav.navigateAbsolute(
                            vm.navBarNavigate(point)
                        )
                    }
                }
                
                override fun onAlertSuccess() {
                    scope.launch {
                        if(alertState == LIST)
                            vm.changeAlertState(CONFIRM)
                        else {
                            vm.deleteChat(
                                chatToDelete,
                                if(profile?.id != chatToDelete?.id)
                                    (alertSelected != 0) else false
                            )
                            vm.changeAlertState(LIST)
                            vm.dismissAlert(false)
                        }
                    }
                }
                
                override fun onChatSwipe(chat: ChatModel) {
                    scope.launch {
                        vm.setChatToDelete(chat)
                        if(profile?.id != chat.userId) {
                            vm.changeAlertState(CONFIRM)
                            vm.dismissAlert(true)
                        } else {
                            vm.setChatToDelete(chat)
                            vm.dismissAlert(true)
                        }
                    }
                }
                
                override fun onAlertDismiss() {
                    scope.launch {
                        vm.dismissAlert(false)
                        vm.changeAlertState(LIST)
                    }
                }
                
                override fun onUnreadChange() {
                    scope.launch {
                        vm.getUnread()
                        vm.changeUnRead()
                    }
                }
                
                override fun onListUpdate() {
                    scope.launch {
                        vm.getDialogs()
                        if(dialogs.size > 6) listState.scrollToItem(
                            dialogs.size - 6
                        )
                    }
                }
                
                override fun onListAlertSelect(index: Int) {
                    scope.launch { vm.alertSelect(index) }
                }
                
                override fun onChatClick(chat: ChatModel) {
                    nav.navigate("chat?id=${chat.id}")
                }
                
                override fun onEndedClick() {
                    scope.launch { vm.changeCompletedState() }
                }
            }
        )
    }
}