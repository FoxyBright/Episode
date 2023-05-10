package ru.rikmasters.gilty.chats.models.ws.enums

enum class AnswerType {
    @Suppress("unused")
    TYPING_MESSAGE,

    DELETE_MESSAGE,
    COMPLETED_CHAT,
    UPDATE_MESSAGE,
    UPDATED_CHATS,
    CHAT_DELETED,
    READ_MESSAGE,
    NEW_MESSAGE,
}
