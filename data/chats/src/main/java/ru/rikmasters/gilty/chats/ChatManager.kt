package ru.rikmasters.gilty.chats

class ChatManager(
    
    private val web: ChatWebSource,
) {
    
    suspend fun getDialogs() {
        web.getDialogs()
    }
    
    suspend fun getMessages(chatId: String) {
        web.getMessages(chatId)
    }
    
    suspend fun getWriters() {
        web.getWriters()
    }
    
    suspend fun sendMessage() {
        web.sendMessage()
    }
}