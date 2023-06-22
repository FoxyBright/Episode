package ru.rikmasters.gilty.chat.presentation.ui.chatList

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.CONFIRM
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chat.viewmodel.ChatListViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.extentions.animateToLastPosition
import ru.rikmasters.gilty.shared.common.extentions.rememberLazyListScrollState
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.SortTypeModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatListScreen(vm: ChatListViewModel) {
    
    val listState = rememberLazyListScrollState("chat_list")
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val unreadNotifications by vm.unreadNotifications.collectAsState()
    val chats = vm.chats.collectAsLazyPagingItems()
    val unreadMessages by vm.unreadMessages.collectAsState()
    val alertSelected by vm.alertSelected.collectAsState()
    val isArchiveOn by vm.isArchiveOn.collectAsState()
    val sortType by vm.sortType.collectAsState()
    val alertState by vm.alertState.collectAsState()
    val completed by vm.completed.collectAsState()
    val chatsCount by vm.chatsCount.collectAsState()
    val alert by vm.alert.collectAsState()
    
    val navBar = remember(
        unreadMessages, unreadNotifications
    ) {
        mutableListOf(
            INACTIVE, unreadNotifications,
            INACTIVE, unreadMessages, INACTIVE
        )
    }
    
    var isFirstRefresh by remember { mutableStateOf(true) }
    
    LaunchedEffect(chats.itemSnapshotList.items) {
        // Scrolls down to last position if it is needed
        if(
            chats.itemSnapshotList.items.isNotEmpty()
            && isFirstRefresh
        ) {
            listState.animateToLastPosition("chat_list")
            isFirstRefresh = false
        }
    }
    
    val back = colorScheme.primaryContainer
    LaunchedEffect(Unit) {
        asm.systemUi.setNavigationBarColor(back)
        vm.getUnread()
        val pref = context
            .getSharedPreferences("sharedPref", MODE_PRIVATE)
        pref.getInt("unread_messages", 0).let {
            vm.setUnreadMessages(it > 0)
        }
        pref.getInt("unread_notification", 0).let {
            vm.setUnreadNotifications(it > 0)
        }
    }
    
    ChatListContent(
        state = ChatListState(
            stateList = navBar,
            chats = chats,
            endedState = completed,
            alertActive = alert,
            alertState = alertState,
            alertSelect = alertSelected,
            sortType = sortType,
            listState = listState,
            isSortOn = sortType != null,
            isArchiveOn = isArchiveOn,
            smthError = false,
            chatsCount = chatsCount
        ),
        callback = object: ChatListCallback {
            override fun onSortClick(sortTypeModel: SortTypeModel?) {
                scope.launch { vm.changeSortType(sortTypeModel) }
            }
            
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
            }
            
            override fun onListAlertSelect(index: Int) {
                scope.launch { vm.alertSelect(index) }
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
