package ru.rikmasters.gilty.chats.repository

import kotlinx.coroutines.flow.map
import ru.rikmasters.gilty.chats.ChatWebSource
import ru.rikmasters.gilty.chats.models.Message
import ru.rikmasters.gilty.chats.models.UserWs
import ru.rikmasters.gilty.chats.websocket.enums.AnswerType
import ru.rikmasters.gilty.chats.websocket.enums.AnswerType.DELETE_MESSAGE
import ru.rikmasters.gilty.chats.websocket.enums.AnswerType.NEW_MESSAGE
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.model.chat.MessageModel

class MessageRepository(
    
    private val chatWebSource: ChatWebSource,
    
    override val primarySource: DbSource,
    
    override val webSource: KtorSource,
): OfflineFirstRepository<WebSource, DbSource>(webSource, primarySource) {
    
    private fun List<Message>.map() = this
        .sortedByDescending { of(it.createdAt).millis() }
        .map { it.map() }
    
    fun messageFlow() = primarySource
        .listenAll(Message::class)
        .map { it.map() }
    
    fun writersFlow() = primarySource
        .listenAll(UserWs::class)
        .map { list ->
            list.map {
                Pair(it.id, it.thumbnail.map())
            }
        }
    
    suspend fun deleteWriter(id: String) {
        primarySource.deleteAll<UserWs>()
        // TODO удалять только того чей срок истек
    }
    
    suspend fun writersUpdate(user: UserWs) {
        primarySource.save(user)
    }
    
    suspend fun messageUpdate(answer: Pair<AnswerType, Any?>?) {
        answer?.let { (type, model) ->
            model?.let {
                
                when(type) {
                    NEW_MESSAGE -> primarySource.save(it as Message)
                    DELETE_MESSAGE -> {
                        // TODO Удаление сообщения из локального хранилища
                        //primarySource.deleteById<Message>(it as String)
                    }
                    
                    else -> {
                        // TODO Пометка сообщения как прочитанного
//                        primarySource.findById<Message>(it.toString())
//                            ?.let { mes -> primarySource.save(mes.copy(isRead = true)) }
                    }
                }
                
                if(type == NEW_MESSAGE) primarySource.save(it as Message)
            }
        }
    }
    
    suspend fun getMessages(
        id: String, forceWeb: Boolean,
    ): List<MessageModel> {
        
        if(!forceWeb) primarySource
            .findAll<Message>()
            .filter { it.chatId == id }
            .let { if(it.isNotEmpty()) return it.map() }
        
        val list = chatWebSource
            .getMessages(id, 0, null)
        
        primarySource.deleteAll<Message>()
        primarySource.saveAll(list)
        
        return list.map()
    }
}