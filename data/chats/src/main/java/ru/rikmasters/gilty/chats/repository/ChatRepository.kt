package ru.rikmasters.gilty.chats.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import ru.rikmasters.gilty.chats.models.chat.Chat
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*
import ru.rikmasters.gilty.notification.paginator.Paginator
import ru.rikmasters.gilty.notification.paginator.PagingManager
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.wrapper.ResponseWrapper

open class ChatRepository(
    override val primarySource: DbSource,
    override val webSource: ChatWebSource,
): OfflineFirstRepository<WebSource, DbSource>(
    webSource, primarySource
) {
    
    // мапер листа DTO -> UI моделей
    private fun List<Chat>.map() = this.map { it.map() }
    
    // чат удалился или обновился
    suspend fun chatUpdate(answer: Pair<AnswerType, Any?>?) {
        answer?.let { (type, model) ->
                if(type == AnswerType.UPDATED_CHATS)
                    primarySource.save(model as Chat)
                else
                    primarySource.deleteById<Chat>(model!!)
            }
        refresh()
    }
    
    // удаление чата
    suspend fun deleteChat(id: String) {
        primarySource.deleteById<Chat>(id)
    }
    
    // менеджер пагинации для чатов
    private class ChatPagingManager(
        private val source: ChatRepository,
    ): PagingManager<ChatModel> {
        
        // метод получения списка чатов
        override suspend fun getPage(
            page: Int, perPage: Int,
        ) = Pair(
            source.uploadChats(page, perPage),
            ResponseWrapper.Paginator(page, perPage)
        )
    }
    
    // источник пагинации для чатов
    private class ChatPagingSource(
        manager: ChatPagingManager,
    ): Paginator<ChatModel, ChatPagingManager>(manager)
    
    // последний используемый источник пагинации
    private var source: ChatPagingSource? = null
    
    // создание нового источника пагинации
    private fun newSource(): ChatPagingSource {
        source?.invalidate()
        source = ChatPagingSource(
            ChatPagingManager((this))
        ); return source!!
    }
    
    // обновление списка чатов
    fun refresh() {
        source?.invalidate()
        source = null
    }
    
    // пагинация для чатов
    fun pagination(scope: CoroutineScope) =
        Pager(PagingConfig(5))
        { newSource() }.flow.cachedIn(scope)
    
    // подгрузка чатов по страницам
    private suspend fun uploadChats(
        page: Int, perPage: Int,
    ): List<ChatModel> {
        val list = webSource.getDialogs(page, perPage).first
        primarySource.deleteAll<Chat>()
        primarySource.saveAll(list)
        return list.map()
    }
}