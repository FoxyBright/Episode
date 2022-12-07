package ru.rikmasters.gilty.chat.presentation.ui.chatlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.model.DemoMessageModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel

@Composable
fun ChatListScreen(nav: NavState = get()) {

    val stateList = remember {
        mutableStateListOf(INACTIVE, NEW, INACTIVE, ACTIVE, INACTIVE)
    }

    val chats = listOf(
        Pair(
            DemoFullMeetingModel,
            listOf(DemoMessageModel)
        )
    )

    val state = ChatListState(stateList, chats)

    ChatListContent(state, Modifier, object : ChatListCallback {
        override fun onNavBarSelect(point: Int) {
            repeat(stateList.size) {
                if (it == point) stateList[it] = ACTIVE
                else if (stateList[it] != NEW)
                    stateList[it] = INACTIVE
                when (point) {
                    0 -> nav.navigateAbsolute("main/meetings")
                    1 -> nav.navigateAbsolute("notification/list")
                    2 -> nav.navigateAbsolute("addmeet/category")
                    3 -> nav.navigateAbsolute("chats/main")
                    4 -> nav.navigateAbsolute("profile/main")
                }
            }
        }

        override fun onChatClick(chat: Int) {
            nav.navigate("chat")
        }
    })
}