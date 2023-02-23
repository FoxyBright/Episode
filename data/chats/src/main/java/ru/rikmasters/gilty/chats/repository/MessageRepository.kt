package ru.rikmasters.gilty.chats.repository

import kotlinx.coroutines.flow.map
import ru.rikmasters.gilty.chats.ChatWebSource
import ru.rikmasters.gilty.chats.models.Message
import ru.rikmasters.gilty.chats.websocket.enums.AnswerType
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
    
    suspend fun messageUpdate(answer: Pair<AnswerType, Any?>?) {
        answer?.let { (type, model) ->
            model?.let {
                
                if(type == NEW_MESSAGE) primarySource.save(it as Message)
                
//                if(type == DELETE_MESSAGE) logD("DELETE MyMessage $it")
                //
                //                if(type == READ_MESSAGE) primarySource.findById<Message>(it.toString())
                //                    ?.let { mes -> primarySource.save(mes.copy(isRead = true)) }
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