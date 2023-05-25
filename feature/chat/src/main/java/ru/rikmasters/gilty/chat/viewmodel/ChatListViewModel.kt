package ru.rikmasters.gilty.chat.viewmodel

import android.content.Context
import androidx.activity.ComponentActivity.MODE_PRIVATE
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.SortTypeModel
import ru.rikmasters.gilty.shared.model.chat.SortTypeModel.MESSAGE_DATE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.*

class ChatListViewModel: ViewModel(), PullToRefreshTrait {
    
    private val meetManager by inject<MeetingManager>()
    private val chatManager by inject<ChatManager>()
    
    private val context = getKoin().get<Context>()
    
    private val _completed = MutableStateFlow(false)
    val completed = _completed.asStateFlow()
    
    private val chatToDelete = MutableStateFlow<ChatModel?>(null)
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _alertState = MutableStateFlow(LIST)
    val alertState = _alertState.asStateFlow()
    
    private val _alertSelected = MutableStateFlow(0)
    val alertSelected = _alertSelected.asStateFlow()
    
    private val refresh = MutableStateFlow(false)
    
    private val _sortType = MutableStateFlow<SortTypeModel?>(null)
    val sortType = _sortType.asStateFlow()
    
    private val _isArchiveOn = MutableStateFlow(false)
    val isArchiveOn = _isArchiveOn.asStateFlow()
    
    private val _unreadMessages = MutableStateFlow(
        lazy {
            val count = getKoin().get<Context>()
                .getSharedPreferences(
                    "sharedPref", MODE_PRIVATE
                ).getInt("unread_messages", 0)
            if(count > 0) NEW_ACTIVE else ACTIVE
        }.value
    )
    val unreadMessages = _unreadMessages.asStateFlow()
    suspend fun setUnreadMessages(hasUnread: Boolean) {
        _unreadMessages.emit(if(hasUnread) NEW_ACTIVE else ACTIVE)
    }
    
    suspend fun getUnread() {
        chatManager.updateUnreadMessages().on(
            success = {
                context.getSharedPreferences(
                    "sharedPref",
                    MODE_PRIVATE
                ).edit()
                    .putInt("unread_messages", it)
                    .apply()
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun onChatClick(chatId: String) {
        chatManager.connectToChat(chatId)
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val chats by lazy {
        combine(refresh, _sortType) { refresh, sort ->
            Pair(refresh, sort)
        }.flatMapLatest {
            chatManager.getChats(it.second ?: MESSAGE_DATE)
        }.cachedIn(coroutineScope)
    }
    
    override suspend fun forceRefresh() = singleLoading {
        refresh.value = !refresh.value
    }
    
    suspend fun alertSelect(index: Int) {
        _alertSelected.emit(index)
    }
    
    suspend fun changeSortType(sortType: SortTypeModel?) {
        _sortType.emit(sortType)
    }
    
    suspend fun changeIsArchiveOn() {
        _isArchiveOn.emit(!_isArchiveOn.value)
    }
    
    suspend fun deleteChat(
        forAll: Boolean,
    ) = singleLoading {
        chatToDelete.value?.let {
            chatManager
                .deleteChat(it.id, forAll)
                .on(
                    success = {
                        alertSelect(0)
                        changeAlertState(LIST)
                        dismissAlert(false)
                    },
                    loading = {},
                    error = { e ->
                        context.errorToast(
                            e.serverMessage
                        )
                    }
                )
        }
    }
    
    suspend fun navBarNavigate(
        point: Int,
    ) = when(point) {
        0 -> "main/meetings"
        1 -> "notification/list"
        2 -> {
            meetManager.clearAddMeet()
            "addmeet/category"
        }
        4 -> "profile/main"
        else -> "chats/main"
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
