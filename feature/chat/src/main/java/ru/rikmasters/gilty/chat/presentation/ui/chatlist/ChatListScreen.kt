package ru.rikmasters.gilty.chat.presentation.ui.chatlist

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.ui.chat.PinnedBarType.MEET
import ru.rikmasters.gilty.chat.presentation.ui.chat.PinnedBarType.MEET_FINISHED
import ru.rikmasters.gilty.chat.presentation.ui.chat.PinnedBarType.TRANSLATION_AWAIT
import ru.rikmasters.gilty.chat.presentation.ui.chatlist.alert.AlertState.CONFIRM
import ru.rikmasters.gilty.chat.presentation.ui.chatlist.alert.AlertState.LIST
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.R.string.delete_my_and_other_chat_button
import ru.rikmasters.gilty.shared.R.string.delete_my_chat_button
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.getChatWithData
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW

@Composable
fun ChatListScreen(nav: NavState = get()) {
    
    val stateList = remember {
        mutableStateListOf(INACTIVE, NEW, INACTIVE, ACTIVE, INACTIVE)
    }
    
    val chatsList = remember {
        mutableStateListOf(
            getChatWithData(id = "1", dateTime = NOW_DATE, isOnline = true, hasUnread = true),
            getChatWithData(id = "2", dateTime = TOMORROW),
            getChatWithData(id = "3", dateTime = NOW_DATE, hasUnread = true),
            getChatWithData(id = "4", dateTime = YESTERDAY, isOnline = true),
            getChatWithData(id = "5", dateTime = YESTERDAY, hasUnread = true)
        )
    }
    
    val chats = getSortedChats(chatsList)
    
    var ended by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
                if(state == LIST) state = CONFIRM
                else {
                    val select = list.indexOf(list.first { it.second })
                    chatsList.remove(chatToDelete.value)
                    Toast.makeText(
                        context,
                        "Чат ${chatToDelete.value?.title} был удален ${
                            when(select) {
                                0 -> "у вас"
                                else -> "у всех участников"
                            }
                        }",
                        Toast.LENGTH_SHORT
                    ).show()
                    state = LIST
                    active = false
                }
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
                    LocalDate.of(chat.dateTime)
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