package ru.rikmasters.gilty.chats.repository

import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.chats.models.chat.Chat
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType
import ru.rikmasters.gilty.chats.paging.ChatPagingSource
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*
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
    
    // последний используемый источник пагинации
    private var source: ChatPagingSource? = null
    
    // создание нового источника пагинации
    fun newSource(manager: ChatManager): ChatPagingSource {
        source?.invalidate()
        source = ChatPagingSource(manager)
        return source!!
    }
    
    // обновление списка чатов
    fun refresh() {
        source?.invalidate()
        source = null
    }
    
    // подгрузка чатов по страницам
    suspend fun getChats(
        page: Int, perPage: Int,
    ): Pair<List<ChatModel>, ResponseWrapper.Paginator> {
        val list = webSource.getDialogs(page, perPage)
        primarySource.deleteAll<Chat>()
        primarySource.saveAll(list.first)
        return Pair(list.first.map(), list.second)
    }
}