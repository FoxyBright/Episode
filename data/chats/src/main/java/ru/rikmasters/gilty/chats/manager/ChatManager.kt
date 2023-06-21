package ru.rikmasters.gilty.chats.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity.MODE_PRIVATE
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
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
    
    suspend fun updateUnread(
        context: Context,
        clear: Boolean = false,
        mes: String = "unread_messages",
        not: String = "unread_notification",
        shared: SharedPreferences = context
            .getSharedPreferences(
                "sharedPref", MODE_PRIVATE
            ),
        empty: Triple<Int, Int, Int?> =
            Triple(0, 0, null),
    ) = withContext(IO) {
        if(!clear) webSource.getChatsStatus().on(
            success = { status ->
                shared.edit()
                    .putInt(not, status.notificationsUnread)
                    .putInt(mes, status.unreadCount)
                    .apply()
                    .let {
                        Triple(
                            status.notificationsUnread,
                            status.unreadCount,
                            status.chatsCount
                        )
                    }
            },
            loading = { empty },
            error = { empty }
        ) else shared.edit()
            .putInt(mes, 0)
            .putInt(not, 0)
            .apply()
            .let { empty }
    }
    
    fun getChats(
        sortTypeModel: SortTypeModel,
        isArchiveOn: Boolean,
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
                    sortType = sortTypeModel.mapDTO(),
                    isArchiveOn = isArchiveOn
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
    ) = withContext(IO) {
        if(!forAll) store.deleteChat(chatId)
        webSource.deleteChat(chatId, forAll)
    }
    
    // подключение к веб сокетам
    suspend fun connectWebSocket(
        userId: String,
    ) = single(JOIN) {
        webSocket.connect(
            userId = userId
        )
    }
    
    @Suppress("unused")
    // заблокировать уведомления от чата
    suspend fun muteChatNotifications(
        chatId: String, unmuteAt: String,
    ) = withContext(IO) {
        webSource.muteChatNotifications(
            chatId = chatId,
            unmuteAt = unmuteAt
        )
    }
    
    @Suppress("unused")
    suspend fun unmuteChatNotifications(
        chatId: String,
    ) = withContext(IO) {
        webSource.unmuteChatNotifications(
            chatId = chatId
        )
    }
    
    @Suppress("unused")
    suspend fun getChatAlbum(
        chatId: String,
    ) = withContext(IO) {
        webSource.getChatAlbum(
            chatId = chatId
        )
    }
}
