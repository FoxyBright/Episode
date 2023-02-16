package ru.rikmasters.gilty.chat.presentation.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.chats.ChatManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.chat.ChatModel

class ChatListViewModel: ViewModel() {
    
    private val chatsManager by inject<ChatManager>()
    
    private val _dialogs = MutableStateFlow(emptyList<ChatModel>())
    val dialogs = _dialogs.asStateFlow()
    
    suspend fun getDialogs() = singleLoading {
        _dialogs.emit(chatsManager.getDialogs())
    }
}
