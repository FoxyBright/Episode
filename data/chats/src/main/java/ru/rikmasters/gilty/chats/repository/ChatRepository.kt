package ru.rikmasters.gilty.chats.repository

import ru.rikmasters.gilty.chats.models.chat.Chat
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType.UPDATED_CHATS
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.data.source.WebSource
import ru.rikmasters.gilty.core.data.source.deleteById

open class ChatRepository(
    override val primarySource: DbSource,
    override val webSource: ChatWebSource,
): OfflineFirstRepository<WebSource, DbSource>(
    webSource,
    primarySource
) {
    
    // чат удалился или обновился
    suspend fun chatUpdate(answer: Pair<AnswerType, Any?>?) {
        answer?.let { (type, data) ->
            data?.let { model ->
                if(type == UPDATED_CHATS)
                    primarySource.save(model as Chat)
                else
                    primarySource.deleteById<Chat>(model)
            }
        }
    }
    
    // удаление чата
    suspend fun deleteChat(id: String) {
        primarySource.deleteById<Chat>(id)
    }
}
