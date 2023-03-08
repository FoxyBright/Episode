package ru.rikmasters.gilty.chat.presentation.ui.chatList

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.CONFIRM
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chat.viewmodel.ChatListViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.chat.ChatModel

@Composable
fun ChatListScreen(vm: ChatListViewModel) {
    
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val nav = get<NavState>()
    
    val alertSelected by vm.alertSelected.collectAsState()
    val chats = vm.chats.collectAsLazyPagingItems()
    val unreadChats by vm.unreadChats.collectAsState()
    val alertState by vm.alertState.collectAsState()
    val completed by vm.completed.collectAsState()
    val navBar by vm.navBar.collectAsState()
    val unRead by vm.unread.collectAsState()
    val alert by vm.alert.collectAsState()
    
    LaunchedEffect(chats.loadState) {
        val loadState = chats.loadState
        vm.isPageRefreshing.emit(
            loadState.append is LoadState.Loading
                    || loadState.refresh is LoadState.Loading
        )
    }
    
    ChatListContent(
        ChatListState(
            navBar, chats, completed, alert,
            alertState, alertSelected, unRead,
            listState, unreadChats
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
                    else
                        vm.deleteChat((alertSelected != 0))
                }
            }
            
            override fun onChatSwipe(chat: ChatModel) {
                scope.launch {
                    vm.setChatToDelete(chat)
                    if(chat.userId != chat.organizer.id) {
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
                scope.launch { vm.forceRefresh() }
            }
            
            override fun onListAlertSelect(index: Int) {
                scope.launch { vm.alertSelect(index) }
            }
            
            override fun onChatClick(chat: ChatModel) {
                scope.launch { vm.onChatClick(chat.id) }
                nav.navigate("chat?id=${chat.id}")
            }
            
            override fun onEndedClick() {
                scope.launch { vm.changeCompletedState() }
            }
        }
    )
}