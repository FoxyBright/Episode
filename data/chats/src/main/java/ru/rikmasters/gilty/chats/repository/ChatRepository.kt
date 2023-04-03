package ru.rikmasters.gilty.chats.repository

import ru.rikmasters.gilty.chats.models.chat.Chat
import ru.rikmasters.gilty.chats.models.ws.enums.AnswerType
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*

open class ChatRepository(
    override val primarySource: DbSource,
    override val webSource: ChatWebSource
) : OfflineFirstRepository<WebSource, DbSource>(
    webSource,
    primarySource
) {

    // мапер листа DTO -> UI моделей
    private fun List<Chat>.map() = this.map { it.map() }

    // чат удалился или обновился
    suspend fun chatUpdate(answer: Pair<AnswerType, Any?>?) {
        answer?.let { (type, model) ->
            if (type == AnswerType.UPDATED_CHATS) {
                primarySource.save(model as Chat)
            } else {
                primarySource.deleteById<Chat>(model!!)
            }
        }
    }

    // удаление чата
    suspend fun deleteChat(id: String) {
        primarySource.deleteById<Chat>(id)
    }
}
