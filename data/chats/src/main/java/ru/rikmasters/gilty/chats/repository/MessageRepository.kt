package ru.rikmasters.gilty.chats.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import ru.rikmasters.gilty.chats.models.message.Message
import ru.rikmasters.gilty.chats.models.ws.UserWs
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType.DELETE_MESSAGE
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType.NEW_MESSAGE
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.notification.paginator.Paginator
import ru.rikmasters.gilty.notification.paginator.PagingManager
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.wrapper.ResponseWrapper

class MessageRepository(
    override val primarySource: DbSource,
    override val webSource: ChatWebSource,
): OfflineFirstRepository<KtorSource, DbSource>(
    webSource, primarySource
) {
    
    // мапер листа DTO -> UI моделей
    private fun List<Message>.map() =
        this.sortedByDescending { of(it.createdAt).millis() }.map { it.map() }
    
    // поток пишущих
    fun writersFlow() = primarySource.listenAll(UserWs::class)
        .map { list -> list.map { Pair(it.id, it.thumbnail.map()) } }
    
    // кто-то больше не пишет
    suspend fun deleteWriter(id: String) = primarySource.deleteById<UserWs>(id)
    
    // кто-то новый пишет
    suspend fun writersUpdate(user: UserWs) = primarySource.save(user)
    
    // удалено прочитано или добавлено сообщение в чат
    suspend fun messageUpdate(answer: Pair<AnswerType, Any?>?) =
        answer?.let { (type, model) ->
            when(type) {
                NEW_MESSAGE -> primarySource.save(model as Message)
                DELETE_MESSAGE -> primarySource.deleteById<Message>(model!!)
                else -> primarySource
                    .findById<Message>(model!!)
                    ?.let { primarySource.save(it.copy(isRead = true)) }
            }
            refresh()
        }
    
    // менеджер пагинации для сообщений
    private class MessagePagingManager(
        private val chatId: String,
        private val source: MessageRepository,
    ): PagingManager<MessageModel> {
        
        // метод получения списка сообщений
        override suspend fun getPage(
            page: Int, perPage: Int,
        ) = Pair(
            source.uploadMessage(chatId, page, perPage),
            ResponseWrapper.Paginator(page, perPage)
        )
    }
    
    // источник пагинации для сообщений
    private class MessagesPagingSource(
        manager: MessagePagingManager,
    ): Paginator<MessageModel, MessagePagingManager>(manager)
    
    // последний используемый источник пагинации
    private var source: MessagesPagingSource? = null
    
    // создание нового источника пагинации
    private fun newSource(chatId: String): MessagesPagingSource {
        source?.invalidate()
        source = MessagesPagingSource(
            MessagePagingManager(chatId, (this))
        ); return source!!
    }
    
    // обновление списка сообщений
    private fun refresh() {
        source?.invalidate()
        source = null
    }
    
    // пагинация для сообщений
    fun pagination(
        chatId: String, scope: CoroutineScope,
    ) = Pager(PagingConfig(15))
    { newSource(chatId) }.flow.cachedIn(scope)
    
    // подгрузка сообщений по страницам
    private suspend fun uploadMessage(
        chatId: String, page: Int, perPage: Int,
    ): List<MessageModel> {
        val list = webSource.getMessages(chatId, page, perPage)
        primarySource.deleteAll<Message>()
        primarySource.saveAll(list.first)
        return list.first.map()
    }
}