package ru.rikmasters.gilty.chats.models

data class MarkAsReadRequest(
    val messageIds: List<String>,
    val readAll: Boolean,
)
