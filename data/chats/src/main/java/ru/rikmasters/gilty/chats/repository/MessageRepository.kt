package ru.rikmasters.gilty.chats.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import ru.rikmasters.gilty.chats.models.message.Message
import ru.rikmasters.gilty.chats.models.ws.UserWs
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType.DELETE_MESSAGE
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType.NEW_MESSAGE
import ru.rikmasters.gilty.chats.paging.ChatMessagesPagingSource
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.models.Profile

class MessageRepository(
    override val primarySource: DbSource,
    override val webSource: ChatWebSource,
): OfflineFirstRepository<KtorSource, DbSource>(
    webSource, primarySource
) {
    
    // поток пишущих
    fun writersFlow() = primarySource.listenAll(UserWs::class)
        .map { list -> list.map { Pair(it.id, it.thumbnail.map()) } }
        
    suspend fun getUser() = primarySource.find<Profile>()?.id
    
    // кто-то больше не пишет
    suspend fun deleteWriter(id: String) = primarySource.deleteById<UserWs>(id)
    
    // кто-то новый пишет
    suspend fun writersUpdate(user: UserWs) = primarySource.save(user)
    
    // удалено прочитано или добавлено сообщение в чат
    suspend fun messageUpdate(answer: Pair<AnswerType, Any?>?) =
        answer?.let { (type, data) ->
            data?.let { model ->
                when(type) {
                    NEW_MESSAGE -> primarySource.save(model as Message)
                    DELETE_MESSAGE -> primarySource.deleteById<Message>(model)
                    else -> primarySource.findById<Message>(model)?.let {
                        primarySource.save(it.copy(isRead = true))
                    }
                }
            }
            refresh()
        }
    
    private val refreshTrigger = MutableStateFlow(false)
    
    private fun refresh() {
        refreshTrigger.value = !refreshTrigger.value
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMessagesPaging(
        chatId: String,
    ): Flow<PagingData<MessageModel>> = refreshTrigger.flatMapLatest {
        Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ChatMessagesPagingSource(
                    webSource = webSource,
                    chatId = chatId
                )
            }
        ).flow
    }
}