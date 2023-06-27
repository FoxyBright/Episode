package ru.rikmasters.gilty.chats.paging.messages.dao

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import ru.rikmasters.gilty.chats.paging.messages.entity.DBMessage

@Dao
interface MessagesDao {
    
    @Insert(onConflict = REPLACE)
    suspend fun insertAll(messages: List<DBMessage>)
    
    @Insert(onConflict = REPLACE)
    suspend fun addMessage(message: DBMessage)
    
    @Query("SELECT * FROM messages")
    fun getMessages(): PagingSource<Int, DBMessage>
    
    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()
}