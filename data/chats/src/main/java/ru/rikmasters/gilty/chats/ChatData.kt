package ru.rikmasters.gilty.chats

import androidx.room.Room
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.chats.manager.MessageManager
import ru.rikmasters.gilty.chats.models.chat.Chat
import ru.rikmasters.gilty.chats.models.message.Message
import ru.rikmasters.gilty.chats.models.ws.UserWs
import ru.rikmasters.gilty.chats.paging.messages.MessageDataBase
import ru.rikmasters.gilty.chats.repository.ChatRepository
import ru.rikmasters.gilty.chats.repository.MessageRepository
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.chats.source.websocket.ChatWebSocket
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition

object ChatData: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity<Message>()
        entity<Chat>()
        entity<UserWs>()
    }
    
    override fun Module.koin() {
        
        single {
            Room.databaseBuilder(
                get(),
                MessageDataBase::class.java,
                "messages"
            ).build()
        }
        
        single {
            get<MessageDataBase>()
                .getRemoteKeysDao()
        }
        
        single {
            get<MessageDataBase>()
                .getMessagesDao()
        }
        
        singleOf(::MessageRepository)
        singleOf(::ChatWebSocket)
        singleOf(::ChatRepository)
        singleOf(::MessageManager)
        singleOf(::ChatWebSource)
        singleOf(::ChatManager)
    }
}