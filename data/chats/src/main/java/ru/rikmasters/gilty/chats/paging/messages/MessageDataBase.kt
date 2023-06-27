package ru.rikmasters.gilty.chats.paging.messages

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.rikmasters.gilty.chats.paging.messages.dao.MessageRemoteKeysDao
import ru.rikmasters.gilty.chats.paging.messages.dao.MessagesDao
import ru.rikmasters.gilty.chats.paging.messages.entity.DBMessage
import ru.rikmasters.gilty.chats.paging.messages.entity.MessageRemoteKeys

@Database(
    entities = [DBMessage::class, MessageRemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class MessageDataBase: RoomDatabase() {
    
    abstract fun getMessagesDao(): MessagesDao
    abstract fun getRemoteKeysDao(): MessageRemoteKeysDao
}