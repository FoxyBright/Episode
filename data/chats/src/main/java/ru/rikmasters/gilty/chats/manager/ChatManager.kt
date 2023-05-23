package ru.rikmasters.gilty.chats.manager

import android.content.Context
import androidx.activity.ComponentActivity.MODE_PRIVATE
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import ru.rikmasters.gilty.chats.ChatData.getKoin
import ru.rikmasters.gilty.chats.models.chat.mapDTO
import ru.rikmasters.gilty.chats.paging.ChatListPagingSource
import ru.rikmasters.gilty.chats.repository.ChatRepository
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.chats.source.websocket.ChatWebSocket
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.core.viewmodel.Strategy.JOIN
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.SortTypeModel

class ChatManager(
    private val store: ChatRepository,
    private val webSocket: ChatWebSocket,
    private val webSource: ChatWebSource,
): CoroutineController() {
    
    suspend fun updateUnreadMessages() {
        val count = webSource.getChatsStatus()
        getKoin().get<Context>().getSharedPreferences(
            "sharedPref", MODE_PRIVATE
        ).edit()
            .putInt("unread_messages", count)
            .apply()
    }
    
    fun getChats(
        sortTypeModel: SortTypeModel,
    ): Flow<PagingData<ChatModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                ChatListPagingSource(
                    webSource = webSource,
                    sortType = sortTypeModel.mapDTO()
                )
            }
        ).flow
    }
    
    // подписка на получение сообщений из выбранного чата
    suspend fun connectToChat(chatId: String) {
        withContext(IO) {
            webSocket.connectToChat(chatId)
        }
    }
    
    // отключение от веб сокетов
    fun disconnectWebSocket() {
        webSocket.disconnect()
    }
    
    // удаление чата
    suspend fun deleteChat(
        chatId: String,
        forAll: Boolean,
    ) {
        withContext(IO) {
            webSource.deleteChat(chatId, forAll)
            if(!forAll) store.deleteChat(chatId)
        }
    }
    
    // подключение к веб сокетам
    suspend fun connectWebSocket(userId: String) = single(JOIN) {
        webSocket.connect(userId)
    }
    
    @Suppress("unused")
    // разблокировать уведомления от чата
    suspend fun unmuteChatNotifications(chatId: String) {
        withContext(IO) {
            webSource.unmuteChatNotifications(
                chatId
            )
        }
    }
    
    @Suppress("unused")
    // заблокировать уведомления от чата
    suspend fun muteChatNotifications(
        chatId: String,
        unmuteAt: String,
    ) {
        withContext(IO) {
            webSource.muteChatNotifications(
                chatId, unmuteAt
            )
        }
    }
    
    @Suppress("unused")
    // получить альбом медиа чата
    suspend fun getChatAlbum(
        chatId: String,
    ) = withContext(IO) {
        webSource.getChatAlbum(chatId)
    }
}
