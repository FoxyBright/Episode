package ru.rikmasters.gilty.chats

class ChatManager(
    
    private val web: ChatWebSource,
) {
    
    suspend fun getDialogs(
        page: Int? = null, perPage: Int? = null,
    ) = web.getDialogs(page, perPage)
    
    suspend fun deleteDialog(
        chatId: String,
        forAll: Boolean,
    ) {
        web.deleteDialog(chatId, forAll)
    }
    
    suspend fun unmuteChatNotifications(chatId: String) {
        web.unmuteChatNotifications(chatId)
    }
    
    suspend fun getChatsStatus() = web.getChatsStatus()
    
    suspend fun muteChatNotifications(
        chatId: String, unmuteAt: String,
    ) {
        web.muteChatNotifications(chatId, unmuteAt)
    }
    
    suspend fun getChatAlbum(chatId: String) =
        web.getChatAlbum(chatId)
    
    suspend fun completeChat(chatId: String) {
        web.completeChat(chatId)
    }
    
    suspend fun isTyping(chatId: String) {
        web.isTyping(chatId)
    }
    
    suspend fun madeScreenshot(chatId: String) {
        web.madeScreenshot(chatId)
    }
    
    suspend fun markAsReadMessage(chatId: String) {
        web.markAsReadMessage(chatId)
    }
    
    suspend fun deleteMessage(
        chatId: String,
        messageIds: List<String>,
        allMembers: Boolean,
    ) {
        web.deleteMessage(
            chatId, messageIds,
            allMembers.compareTo(false)
        )
    }
    
    suspend fun sendMessage(chatId: String) {
        web.sendMessage(chatId)
    }
    
    suspend fun getChat(chatId: String) =
        web.getChat(chatId)
    
    suspend fun getMessages(
        chatId: String,
        page: Int? = null,
        perPage: Int? = null,
    ) = web.getMessages(chatId, page, perPage)
}