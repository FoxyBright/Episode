package ru.rikmasters.gilty.chats.models.message

data class MarkAsReadRequest(
    val messageIds: List<String>,
    val readAll: Boolean,
)
