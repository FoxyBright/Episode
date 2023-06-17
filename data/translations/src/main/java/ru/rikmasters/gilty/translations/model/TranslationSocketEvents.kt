package ru.rikmasters.gilty.translations.model

enum class TranslationsSocketEvents(val value: String) {
    PONG("pusher:pong"),
    PING("pusher:ping"),
    CONNECTION_ESTABLISHED("pusher:connection_established"),
    SUBSCRIPTION_SUCCEEDED("pusher_internal:subscription_succeeded"),
    SUBSCRIBE("pusher:subscribe"),
    UNSUBSCRIBE("pusher:unsubscribe"),
    TRANSLATION_STARTED("translation.started"),
    TRANSLATION_COMPLETED("translation.completed"),
    SIGNAL("translation.signal"),
    APPEND_TIME("translation.extension"),
    EXPIRED("translation.expired"),
    USER_CONNECTED("user.connected"),
    USER_DISCONNECTED("user.disconnected"),
    USER_KICKED("user.kicked"),
    MESSAGE_SENT("message.sent");
    companion object {
        infix fun from(value: String) = values()
            .firstOrNull { it.value == value }
    }
}