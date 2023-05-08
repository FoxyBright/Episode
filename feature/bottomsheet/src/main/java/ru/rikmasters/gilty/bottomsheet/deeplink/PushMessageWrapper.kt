package ru.rikmasters.gilty.bottomsheet.deeplink

import ru.rikmasters.gilty.meetings.mapper

object PushMessageWrapper {
    
    private data class PushChat(val id: String)
    private data class PushChatMessage(val chat: PushChat)
    
    enum class LinkType(val value: String) {
        OTHER("OTHER"),
        MEET("meet/?"),
        MESSAGE("CHAT_MESSAGE"),
        MEETING_CREATED("MEETING_CREATED")
    }
    // Data: {type=MEETING_CREATED, content={"meeting":{"id":"ef9aaa75-2bbf-4a01-ad77-8e867918f544","title":"DEBUG",
    
    fun getChatId(data: String, type: LinkType) =
        mapper.readValue(
            data.substringAfter(type.value),
            PushChatMessage::class.java
        ).chat.id
}