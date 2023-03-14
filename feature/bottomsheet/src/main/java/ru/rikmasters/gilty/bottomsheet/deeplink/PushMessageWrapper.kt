package ru.rikmasters.gilty.bottomsheet.deeplink

import ru.rikmasters.gilty.meetings.mapper

object PushMessageWrapper {
    
    private data class PushChat(val id: String)
    private data class PushChatMessage(val chat: PushChat)
    
    enum class LinkType(val value: String) {
        OTHER("OTHER"),
        MEET("meet/?"),
        MESSAGE("CHAT_MESSAGE")
    }
    
    fun getChatId(data: String, type: LinkType) =
        mapper.readValue(
            data.substringAfter(type.value),
            PushChatMessage::class.java
        ).chat.id
}