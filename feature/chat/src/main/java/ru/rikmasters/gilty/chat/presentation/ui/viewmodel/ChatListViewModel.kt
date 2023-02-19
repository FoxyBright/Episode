package ru.rikmasters.gilty.chat.presentation.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chats.ChatManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class ChatListViewModel: ViewModel() {
    
    private val chatsManager by inject<ChatManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val _dialogs = MutableStateFlow(emptyList<ChatModel>())
    val dialogs = _dialogs.asStateFlow()
    
    private val _unreadDialogs = MutableStateFlow(emptyList<ChatModel>())
    val unreadDialogs = _unreadDialogs.asStateFlow()
    
    private val _completed = MutableStateFlow(false)
    val completed = _completed.asStateFlow()
    
    private val _chatToDelete = MutableStateFlow<ChatModel?>(null)
    val chatToDelete = _chatToDelete.asStateFlow()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _alertState = MutableStateFlow(LIST)
    val alertState = _alertState.asStateFlow()
    
    private val _alertSelected = MutableStateFlow(0)
    val alertSelected = _alertSelected.asStateFlow()
    
    private val _unread = MutableStateFlow(false)
    val unread = _unread.asStateFlow()
    
    private val navBarStateList = listOf(
        INACTIVE, NEW, INACTIVE, ACTIVE, INACTIVE
    )
    
    private val _profile = MutableStateFlow<ProfileModel?>(null)
    val profile = _profile.asStateFlow()
    
    private val _navBar = MutableStateFlow(navBarStateList)
    val navBar = _navBar.asStateFlow()
    
    private var page = 1
    
    suspend fun getDialogs() = singleLoading {
        val list = (chatsManager
            .getDialogs(page) + dialogs.value)
        _dialogs.emit(list)
        page++
    }
    
    suspend fun getUnread() = singleLoading {
        _unreadDialogs.emit(dialogs.value)
    }
    
    suspend fun alertSelect(index: Int) {
        _alertSelected.emit(index)
    }
    
    suspend fun changeUnRead() {
        _unread.emit(!unread.value)
    }
    
    private suspend fun navBarSetStates(
        states: List<NavIconState>,
    ) {
        _navBar.emit(states)
    }
    
    
    suspend fun getProfile() = singleLoading {
        _profile.emit(profileManager.getProfile())
    }
    
    suspend fun deleteChat(
        chat: ChatModel?, forAll: Boolean,
    ) = singleLoading {
        chatToDelete.value?.let {
            chatsManager.deleteDialog(it.id, forAll)
            _chatToDelete.emit(chat)
        }
        getDialogs()
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
        return when(point) {
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
        _chatToDelete.emit(chat)
    }
    
    suspend fun changeAlertState(state: AlertState) {
        _alertState.emit(state)
    }
    
    suspend fun changeCompletedState() {
        _completed.emit(!completed.value)
    }
}
