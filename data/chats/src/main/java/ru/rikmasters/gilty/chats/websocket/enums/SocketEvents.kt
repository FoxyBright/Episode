package ru.rikmasters.gilty.chats.websocket.enums

enum class SocketEvents(val value: String) {
    
    PONG("pusher:pong"),
    PING("pusher:ping"),
    CONNECTION_ESTABLISHED("pusher:connection_established"),
    SUBSCRIPTION_SUCCEEDED("pusher_internal:subscription_succeeded"),
    SUBSCRIBE("pusher:subscribe"),
    UNSUBSCRIBE("pusher:unsubscribe"),
    CHATS_UPDATED("chats.updated"),
    CHATS_DELETED("chats.deleted"),
    CHAT_COMPLETED("chat.completed"),
    MESSAGE_SENT("message.sent"),
    MESSAGE_READ("message.read"),
    MESSAGE_DELETED("message.deleted"),
    MESSAGE_TYPING("message.typing"),
    MESSAGE_UPDATE("message.update");
    
    companion object {
        
        infix fun from(value: String) = values()
            .firstOrNull { it.value == value }
    }
}