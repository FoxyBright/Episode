package ru.rikmasters.gilty.chats

import ru.rikmasters.gilty.data.ktor.KtorSource

class ChatWebSource: KtorSource() {
    
    suspend fun getDialogs() {
    }
    
    suspend fun getMessages(chatId: String) {
    }
    
    suspend fun getWriters() {
    }
    
    suspend fun sendMessage() {
    }
    
}