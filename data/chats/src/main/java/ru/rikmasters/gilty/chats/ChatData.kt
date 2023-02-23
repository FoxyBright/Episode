package ru.rikmasters.gilty.chats

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.chats.models.Chat
import ru.rikmasters.gilty.chats.models.Message
import ru.rikmasters.gilty.chats.repository.ChatRepository
import ru.rikmasters.gilty.chats.repository.MessageRepository
import ru.rikmasters.gilty.chats.websocket.WebSocketHandler
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition

object ChatData: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity<Message>()
        entity<Chat>()
    }
    
    override fun Module.koin() {
        singleOf(::MessageRepository)
        singleOf(::WebSocketHandler)
        singleOf(::ChatRepository)
        singleOf(::ChatWebSource)
        singleOf(::ChatManager)
    }
}