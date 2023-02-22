package ru.rikmasters.gilty.chats

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.chats.repository.ChatRepository
import ru.rikmasters.gilty.chats.websocket.WebSocketHandler
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.shared.models.chats.Chat

object ChatData: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity<Chat>()
    }
    
    override fun Module.koin() {
        singleOf(::WebSocketHandler)
        singleOf(::ChatRepository)
        singleOf(::ChatWebSource)
        singleOf(::ChatManager)
    }
}