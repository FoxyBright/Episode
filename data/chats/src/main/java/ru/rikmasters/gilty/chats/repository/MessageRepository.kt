package ru.rikmasters.gilty.chats.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.model.chat.MessageModel

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
                NEW_MESSAGE -> {
                    Log.d("TESTG","new Message ${model as Message}")
                    primarySource.save(model as Message)
                }
                DELETE_MESSAGE -> primarySource.deleteById<Message>(model!!)
                else -> primarySource
                    .findById<Message>(model!!)
                    ?.let { primarySource.save(it.copy(isRead = true)) }
            }
            refresh()
        }

    private val refreshTrigger = MutableStateFlow(false)

    private fun refresh() {
        refreshTrigger.value = !refreshTrigger.value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMessagesPaging(
        chatId: String
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