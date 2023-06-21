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
    
    private val _lastSortType = MutableStateFlow<SortTypeModel?>(null)
    
    private val _isArchiveOn = MutableStateFlow(false)
    val isArchiveOn = _isArchiveOn.asStateFlow()
    
    private val _chatsCount = MutableStateFlow(0)
    val chatsCount = _chatsCount.asStateFlow()
    
    private val _unreadMessages = MutableStateFlow(
        lazy {
            val count = context.getSharedPreferences(
                "sharedPref", MODE_PRIVATE
            ).getInt("unread_messages", 0)
            if(count > 0) NEW_ACTIVE else ACTIVE
        }.value
    )
    val unreadMessages = _unreadMessages.asStateFlow()
    suspend fun setUnreadMessages(hasUnread: Boolean) {
        _unreadMessages.emit(if(hasUnread) NEW_ACTIVE else ACTIVE)
        logD("unread -> ${unreadMessages.value}")
    }
    
    private val _unreadNotifications =
        MutableStateFlow(
            lazy {
                val count = context.getSharedPreferences(
                    "sharedPref", MODE_PRIVATE
                ).getInt("unread_notification", 0)
                if(count > 0) NEW_INACTIVE else INACTIVE
            }.value
        )
    val unreadNotifications =
        _unreadNotifications.asStateFlow()
    
    suspend fun setUnreadNotifications(hasUnread: Boolean) {
        _unreadNotifications.emit(
            if(hasUnread) NEW_INACTIVE else INACTIVE
        )
    }
    
    suspend fun getUnread() = chatManager
        .updateUnread(context)
        .let { _chatsCount.emit(it.third ?: 1) }
    // TODO change 1 to 0 when backend will be ready
    
    suspend fun onChatClick(chatId: String) {
        chatManager.connectToChat(chatId)
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val chats by lazy {
        combine(
            refresh,
            _sortType,
            _isArchiveOn
        ) { refresh, sort, isArchiveOn ->
            // TODO When clicking to sort for the first time list should not update
            /* if((sort == null && _lastSortType.value == SortTypeModel.MEETING_DATE) ){
                 Pair(refresh, Pair(isArchiveOn, _lastSortType.value))
             }else if(( _lastSortType.value == null && sort == SortTypeModel.MEETING_DATE)){
                 Pair(refresh, Pair(isArchiveOn, null))
             }else */
            Pair(refresh, Pair(isArchiveOn, sort))
            
        }.flatMapLatest {
            chatManager.getChats(
                sortTypeModel = it.second.second ?: MESSAGE_DATE,
                it.second.first
            )
            
        }.cachedIn(coroutineScope)
    }
    
    override suspend fun forceRefresh() = singleLoading {
        refresh.value = !refresh.value
    }
    
    suspend fun alertSelect(index: Int) {
        _alertSelected.emit(index)
    }
    
    suspend fun changeSortType(sortType: SortTypeModel?) {
        _lastSortType.emit(_sortType.value)
        _sortType.emit(sortType)
        _isArchiveOn.emit(false)
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
