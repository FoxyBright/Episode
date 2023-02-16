package ru.rikmasters.gilty.chat.presentation.ui.chatList

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.ui.chat.navigation.PinnedBarType.MEET
import ru.rikmasters.gilty.chat.presentation.ui.chat.navigation.PinnedBarType.MEET_FINISHED
import ru.rikmasters.gilty.chat.presentation.ui.chat.navigation.PinnedBarType.TRANSLATION_AWAIT
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chat.presentation.ui.viewmodel.ChatListViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.R.string.delete_my_and_other_chat_button
import ru.rikmasters.gilty.shared.R.string.delete_my_chat_button
import ru.rikmasters.gilty.shared.common.extentions.LOCAL_DATE
import ru.rikmasters.gilty.shared.common.extentions.LocalDate
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW

@Composable
fun ChatListScreen(vm: ChatListViewModel) {
    
    val nav = get<NavState>()
    
    val stateList = remember {
        mutableStateListOf(INACTIVE, NEW, INACTIVE, ACTIVE, INACTIVE)
    }
    
    val dialogs by vm.dialogs.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getDialogs()
    }
    
    val chats = getSortedChats(dialogs)
    
    var ended by remember { mutableStateOf(false) }
    var active by
    
    remember { mutableStateOf(false) }
    
    val delForMe = stringResource(delete_my_chat_button)
    val delForOther = stringResource(delete_my_and_other_chat_button)
    val list = remember {
        mutableStateListOf(
            Pair(delForMe, true),
            Pair(delForOther, false)
        )
    }
    val chatToDelete =
        remember { mutableStateOf<ChatModel?>(null) }
    var state by
    remember { mutableStateOf(LIST) }
    
    ChatListContent(
        ChatListState(
            stateList, chats,
            ended, active, state, list
        ), Modifier, object: ChatListCallback {
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
            
            override fun onAlertSuccess() {
                //                if(state == LIST) state = CONFIRM
                //                else {
                //                    val select = list.indexOf(list.first { it.second })
                //                    dialogs.remove(chatToDelete.value)
                //                    Toast.makeText(
                //                        context,
                //                        "Чат ${chatToDelete.value?.title} был удален ${
                //                            when(select) {
                //                                0 -> "у вас"
                //                                else -> "у всех участников"
                //                            }
                //                        }",
                //                        Toast.LENGTH_SHORT
                //                    ).show()
                //                    state = LIST
                //                    active = false
                //                }
            }
            
            override fun onChatSwipe(chat: ChatModel) {
                chatToDelete.value = chat
                active = true
            }
            
            override fun onAlertDismiss() {
                active = false
                state = LIST
            }
            
            override fun listAlertSelect(index: Int) {
                repeat(list.size) {
                    if(it == index) list[it] =
                        Pair(list[it].first, true)
                    else list[it] = Pair(list[it].first, false)
                }
            }
            
            override fun onChatClick(chat: ChatModel) {
                val type = when {
                    LocalDate.of(chat.datetime)
                        .isBefore(LOCAL_DATE) -> MEET_FINISHED
                    
                    chat.isOnline -> TRANSLATION_AWAIT
                    else -> MEET
                }
                nav.navigate("chat?type=${type.name}")
            }
            
            override fun onEndedClick() {
                ended = !ended
            }
        })
}