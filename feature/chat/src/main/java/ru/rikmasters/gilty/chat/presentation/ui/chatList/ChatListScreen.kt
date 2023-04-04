package ru.rikmasters.gilty.chat.presentation.ui.chatList

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.CONFIRM
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chat.viewmodel.ChatListViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.SortTypeModel

@Composable
fun ChatListScreen(vm: ChatListViewModel) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()

    val chats = vm.chats.collectAsLazyPagingItems()
    val alertSelected by vm.alertSelected.collectAsState()
    val alertState by vm.alertState.collectAsState()
    val navBar by vm.navBar.collectAsState()
    val completed by vm.completed.collectAsState()
    val sortType by vm.sortType.collectAsState()
    val alert by vm.alert.collectAsState()

    LaunchedEffect(Unit) { vm.getChatStatus() }

    ChatListContent(
        ChatListState(
            navBar, chats, completed, alert,
            alertState, alertSelected, sortType,
            listState
        ),
        Modifier,
        object : ChatListCallback {

            override fun onNavBarSelect(point: Int) {
                if (point == 3) return
                scope.launch {
                    nav.navigateAbsolute(
                        vm.navBarNavigate(point)
                    )
                }
            }

            override fun onAlertSuccess() {
                scope.launch {
                    if (alertState == LIST) {
                        vm.changeAlertState(CONFIRM)
                    } else {
                        vm.deleteChat((alertSelected != 0))
                    }
                }
            }

            override fun onChatSwipe(chat: ChatModel) {
                scope.launch {
                    vm.setChatToDelete(chat)
                    if (chat.userId != chat.organizer.id) {
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

            override fun onSortTypeChanged(sortType: SortTypeModel) {
                scope.launch {
                    vm.changeSortType(sortType)
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
