package ru.rikmasters.gilty.chats.manager

import kotlinx.coroutines.CoroutineScope
import ru.rikmasters.gilty.chats.repository.ChatRepository
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.chats.source.websocket.WebSocketHandler
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.core.viewmodel.Strategy.JOIN

class ChatManager(
    
    private val chatRepository: ChatRepository,
    
    private val webSocket: WebSocketHandler,
    
    private val webSource: ChatWebSource,
): CoroutineController() {
    
    // список чатов в потоке собраный пагинатором
    fun chats(scope: CoroutineScope) = chatRepository.pagination(scope)
    
    // обновление списка чатов
    fun refresh() = chatRepository.refresh()
    
    // получить список чатов с непрочитанными сообщениями
    suspend fun getUnread() =
        webSource.getDialogs(unread = 1)
            .first.map { it.map() }
    
    // подписка на получение сообщений из выбранного чата
    suspend fun connectToChat(chatId: String) {
        webSocket.connectToChat(chatId)
    }
    
    // удаление чата
    suspend fun deleteChat(
        chatId: String,
        forAll: Boolean,
    ) {
        webSource.deleteChat(chatId, forAll)
        if(!forAll) chatRepository.deleteChat(chatId)
    }
    
    // подключение к веб сокетам
    suspend fun connect(userId: String) = single(JOIN) {
        webSocket.connect(userId)
    }
    
    @Suppress("unused")
    // получить список непрочитанных во всех чатах
    suspend fun getChatsStatus() = webSource.getChatsStatus()
    
    @Suppress("unused")
    // разблокировать уведомления от чата
    suspend fun unmuteChatNotifications(chatId: String) {
        webSource.unmuteChatNotifications(chatId)
    }
    
    @Suppress("unused")
    // заблокировать уведомления от чата
    suspend fun muteChatNotifications(
        chatId: String, unmuteAt: String,
    ) {
        webSource.muteChatNotifications(chatId, unmuteAt)
    }
    
    @Suppress("unused")
    // получить альбом медиа чата
    suspend fun getChatAlbum(chatId: String) =
        webSource.getChatAlbum(chatId)
}