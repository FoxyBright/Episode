package ru.rikmasters.gilty.chat.viewmodel

import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.component.inject
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW

class ChatListViewModel : ViewModel(), PullToRefreshTrait {

    private val chatManager by inject<ChatManager>()

    private val _unreadChats = MutableStateFlow(emptyList<ChatModel>())
    val unreadChats = _unreadChats.asStateFlow()

    private val _completed = MutableStateFlow(false)
    val completed = _completed.asStateFlow()

    private val chatToDelete = MutableStateFlow<ChatModel?>(null)

    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()

    private val _alertState = MutableStateFlow(LIST)
    val alertState = _alertState.asStateFlow()

    private val _alertSelected = MutableStateFlow(0)
    val alertSelected = _alertSelected.asStateFlow()

    private val _unread = MutableStateFlow(false)
    val unread = _unread.asStateFlow()

    val isPageRefreshing = MutableStateFlow(false)
    override val pagingPull = isPageRefreshing

    private val refresh = MutableStateFlow(false)

    private val _navBar = MutableStateFlow(
        listOf(INACTIVE, INACTIVE, INACTIVE, ACTIVE, INACTIVE)
    )
    val navBar = _navBar.asStateFlow()

    suspend fun onChatClick(chatId: String) {
        chatManager.connectToChat(chatId)
    }

    suspend fun getChatStatus() {
        chatManager.getChatsStatus().let {
            if (it > 0) _navBar.emit(
                listOf(
                    INACTIVE,
                    INACTIVE,
                    INACTIVE,
                    NEW,
                    INACTIVE
                )
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val chats by lazy {
        refresh.flatMapLatest {
            Log.d("TEST","flatMap $it")
            chatManager.getChats()
        }
    }

    override suspend fun forceRefresh() = singleLoading {
        Log.d("TESTSS","force refresh")
        refresh.value = !refresh.value
       // chatManager.refresh()
    }

    suspend fun getUnread() = singleLoading {
        _unreadChats.emit(chatManager.getUnread())
    }

    suspend fun alertSelect(index: Int) {
        _alertSelected.emit(index)
    }

    suspend fun changeUnRead() {
        _unread.emit(!unread.value)
    }

    private suspend fun navBarSetStates(
        states: List<NavIconState>
    ) {
        _navBar.emit(states)
    }

    suspend fun deleteChat(
        forAll: Boolean
    ) = singleLoading {
        chatToDelete.value?.let {
            chatManager.deleteChat(it.id, forAll)
        }
        alertSelect(0)
        changeAlertState(LIST)
        dismissAlert(false)
    }

    suspend fun navBarNavigate(point: Int): String {
        val list = arrayListOf<NavIconState>()
        repeat(navBar.value.size) {
            list.add(
                when {
                    navBar.value[it] == NEW -> NEW
                    it == point -> ACTIVE
                    else -> INACTIVE
                }
            )
        }
        navBarSetStates(list)
        return when (point) {
            0 -> "main/meetings"
            1 -> "notification/list"
            2 -> "addmeet/category"
            4 -> "profile/main"
            else -> "chats/main"
        }
    }

    suspend fun dismissAlert(state: Boolean) {
        _alert.emit(state)
    }

    suspend fun setChatToDelete(chat: ChatModel) {
        chatToDelete.emit(chat)
    }

    suspend fun changeAlertState(state: AlertState) {
        _alertState.emit(state)
    }

    suspend fun changeCompletedState() {
        _completed.emit(!completed.value)
    }
}
