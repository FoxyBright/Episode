package ru.rikmasters.gilty.chats.repository

import androidx.paging.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import ru.rikmasters.gilty.chats.models.message.Message
import ru.rikmasters.gilty.chats.models.ws.UserWs
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType.DELETE_MESSAGE
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType.NEW_MESSAGE
import ru.rikmasters.gilty.chats.paging.MessageMediator
import ru.rikmasters.gilty.chats.paging.messages.MessageDataBase
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.models.Profile

class MessageRepository(
    override val primarySource: DbSource,
    override val webSource: ChatWebSource,
    private val messageStorage: MessageDataBase,
): OfflineFirstRepository<KtorSource, DbSource>(
    webSource, primarySource
) {
    
    private val messageDao =
        messageStorage.getMessagesDao()
    
    // поток пишущих
    fun writersFlow() = primarySource
        .listenAll(UserWs::class)
        .map { it.map { u -> u.id to u.thumbnail.map() } }
    
    // кто-то больше не пишет
    suspend fun deleteWriter(id: String) = primarySource
        .deleteById<UserWs>(id)
    
    // кто-то новый пишет
    suspend fun writersUpdate(user: UserWs) =
        primarySource.save(user)
    
    suspend fun getUser() = primarySource
        .find<Profile>()?.id
    
    // удалено прочитано или добавлено сообщение в чат
    suspend fun messageUpdate(answer: Pair<AnswerType, Any?>?) =
        answer?.let { (type, data) ->
            data?.let { model ->
                when(type) {
                    NEW_MESSAGE -> messageDao.addMessage((model as Message).mapToBase())
                    DELETE_MESSAGE -> primarySource.deleteById<Message>(
                        model
                    )
                    else -> primarySource.findById<Message>(
                        model
                    )?.let {
                        primarySource.save(
                            it.copy(
                                otherRead = true
                            )
                        )
                    }
                }
            }
            refresh()
        }
    
    private
    val refreshTrigger =
        MutableStateFlow(false)
    
    private fun refresh() {
        refreshTrigger.value =
            !refreshTrigger.value
    }
    
    @OptIn(
        ExperimentalCoroutinesApi::class,
        ExperimentalPagingApi::class
    )
    fun getMessagesPaging(
        chatId: String, perPage: Int = 15,
    ): Flow<PagingData<MessageModel>> =
        refreshTrigger.flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = perPage,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    messageStorage.getMessagesDao().getMessages()
                },
                remoteMediator = MessageMediator(
                    chatId = chatId,
                    web = webSource,
                    store = messageStorage,
                    perPage = perPage
                )
            ).flow.map { mes ->
                mes.map { it.map() }
            }
        }
}