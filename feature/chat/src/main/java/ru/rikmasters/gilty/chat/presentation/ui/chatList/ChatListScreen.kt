package ru.rikmasters.gilty.chat.presentation.ui.chatList

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.CONFIRM
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chat.viewmodel.ChatListViewModel
import ru.rikmasters.gilty.core.data.source.SharedPrefListener.Companion.listenPreference
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.extentions.rememberLazyListScrollState
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.SortTypeModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE

@Composable
fun ChatListScreen(vm: ChatListViewModel) {
    val listState = rememberLazyListScrollState("chat_list")
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val chats = vm.chats.collectAsLazyPagingItems()
    val alertSelected by vm.alertSelected.collectAsState()
    val alertState by vm.alertState.collectAsState()
    val sortType by vm.sortType.collectAsState()
    val completed by vm.completed.collectAsState()
    val alert by vm.alert.collectAsState()
    val isArchiveOn by vm.isArchiveOn.collectAsState()

    val unreadMessages by vm.unreadMessages.collectAsState()
    val navBar = remember {
        mutableListOf(
            INACTIVE, INACTIVE, INACTIVE,
            unreadMessages, INACTIVE
        )
    }
    
    LaunchedEffect(Unit) {
        context.listenPreference(
            key = "unread_messages",
            defValue = 0
        ) {
            scope.launch {
                vm.setUnreadMessages(it > 0)
            }
        }
        vm.getUnread()
    }
    
    ChatListContent(
        ChatListState(
            navBar, chats, completed, alert,
            alertState, alertSelected, sortType,
            listState,sortType != null, isArchiveOn
        ),
        Modifier,
        object: ChatListCallback {
            
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
                    if(alertState == LIST) {
                        vm.changeAlertState(CONFIRM)
                    } else {
                        vm.deleteChat((alertSelected != 0))
                    }
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

            override fun onSortClick(sortTypeModel: SortTypeModel?) {
                scope.launch { vm.changeSortType(sortTypeModel) }
            }

            override fun onArchiveClick() {
                scope.launch { vm.changeIsArchiveOn() }
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
