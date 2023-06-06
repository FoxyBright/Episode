package ru.rikmasters.gilty.chats.models.ws

data class ChatStatus(
    val unreadCount: Int,
    val notificationsUnread: Int,
    val chatsCount: Int? = null,
)