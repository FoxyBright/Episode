package ru.rikmasters.gilty.chats.paging.messages.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import ru.rikmasters.gilty.chats.paging.messages.entity.MessageRemoteKeys

@Dao
interface MessageRemoteKeysDao {
    
    @Suppress("unused")
    @Insert(onConflict = REPLACE)
    suspend fun insertAll(remoteKey: List<MessageRemoteKeys>)
    
    @Insert(onConflict = REPLACE)
    suspend fun addKey(remoteKey: MessageRemoteKeys)
    
    @Query("SELECT * FROM keys WHERE messageId = :id")
    suspend fun getRemoteKeyByMessageId(id: String): MessageRemoteKeys?
    
    @Query("DELETE FROM keys")
    suspend fun clearRemoteKeys()
    
    @Suppress("unused")
    @Query("SELECT createdAt FROM keys ORDER BY createdAt DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}