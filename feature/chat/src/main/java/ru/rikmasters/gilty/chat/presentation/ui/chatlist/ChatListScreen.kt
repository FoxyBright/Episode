package ru.rikmasters.gilty.chat.presentation.ui.chatlist

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.DemoChatListModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW

@Composable
fun ChatListScreen(nav: NavState = get()) {
    
    val stateList = remember {
        mutableStateListOf(INACTIVE, NEW, INACTIVE, ACTIVE, INACTIVE)
    }
    
    val chats =
        getSortedChats(DemoChatListModel)
    
    var ended by remember { mutableStateOf(false) }
    
    ChatListContent(
        ChatListState(stateList, chats, ended),
        Modifier, object: ChatListCallback {
            override fun onNavBarSelect(point: Int) {
                repeat(stateList.size) {
                    if(it == point) stateList[it] = ACTIVE
                    else if(stateList[it] != NEW)
                        stateList[it] = INACTIVE
                    when(point) {
                        0 -> nav.navigateAbsolute("main/meetings")
                        1 -> nav.navigateAbsolute("notification/list")
                        2 -> nav.navigateAbsolute("addmeet/category")
                        3 -> nav.navigateAbsolute("chats/main")
                        4 -> nav.navigateAbsolute("profile/main")
                    }
                }
            }
            
            override fun onChatClick(chat: ChatModel) {
                nav.navigate("chat")
            }
            
            override fun onEndedClick() {
                ended = !ended
            }
        })
}