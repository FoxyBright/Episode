package ru.rikmasters.gilty.chats.paging.messages.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keys")
data class MessageRemoteKeys(
    @PrimaryKey
    val messageId: String,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    val createdAt: Long = System.currentTimeMillis(),
)