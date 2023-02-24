package ru.rikmasters.gilty.chats.repository

import kotlinx.coroutines.flow.map
import ru.rikmasters.gilty.chats.ChatWebSource
import ru.rikmasters.gilty.chats.models.Chat
import ru.rikmasters.gilty.chats.websocket.enums.AnswerType
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.model.chat.ChatModel

class ChatRepository(
    
    private val chatWebSource: ChatWebSource,
    
    override val primarySource: DbSource,
    
    override val webSource: KtorSource,
): OfflineFirstRepository<WebSource, DbSource>(webSource, primarySource) {
    
    private fun List<Chat>.map() = this.map { it.map() }
    
    fun chatsFlow() = primarySource
        .listenAll(Chat::class)
        .map { it.map() }
    
    suspend fun chatUpdate(answer: Pair<AnswerType, Any?>?) {
        answer?.let { (type, model) ->
            model?.let {
                if(type == AnswerType.UPDATED_CHATS)
                    primarySource.save(it as Chat)
                else
                    primarySource.deleteById<Chat>(it)
            }
        }
    }
    
    suspend fun deleteChat(id: String) {
        primarySource.deleteById<Chat>(id)
    }
    
    suspend fun getChatList(forceWeb: Boolean): List<ChatModel> {
        
        if(!forceWeb) primarySource.findAll<Chat>()
            .let { if(it.isNotEmpty()) return it.map() }
        
        val list = chatWebSource
            .getDialogs(0, null)
        
        primarySource.deleteAll<Chat>()
        primarySource.saveAll(list)
        
        return list.map()
    }
}