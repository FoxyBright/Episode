package ru.rikmasters.gilty.chats

import ru.rikmasters.gilty.chats.websocket.WebSocketHandler
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.core.viewmodel.Strategy
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import java.io.File

class ChatManager(
    
    private val webSource: ChatWebSource,
    private val webSocket: WebSocketHandler,
): CoroutineController() {
    
    suspend fun getDialogs(
        page: Int? = null, perPage: Int? = null,
    ) = webSource.getDialogs(page, perPage)
    
    suspend fun deleteDialog(
        chatId: String,
        forAll: Boolean,
    ) {
        webSource.deleteDialog(chatId, forAll)
    }
    
    suspend fun connect(userId: String) = single(Strategy.JOIN) {
        webSocket.connect(userId)
    }
    
    val socketAnswer = webSocket.answer
    
    suspend fun getChatsStatus() = webSource.getChatsStatus()
    
    suspend fun unmuteChatNotifications(chatId: String) {
        webSource.unmuteChatNotifications(chatId)
    }
    
    suspend fun muteChatNotifications(
        chatId: String, unmuteAt: String,
    ) {
        webSource.muteChatNotifications(chatId, unmuteAt)
    }
    
    suspend fun getChatAlbum(chatId: String) =
        webSource.getChatAlbum(chatId)
    
    suspend fun completeChat(chatId: String) {
        webSource.completeChat(chatId)
    }
    
    suspend fun isTyping(chatId: String) {
        webSource.isTyping(chatId)
    }
    
    suspend fun madeScreenshot(chatId: String) {
        webSource.madeScreenshot(chatId)
    }
    
    suspend fun markAsReadMessage(chatId: String) {
        webSource.markAsReadMessage(chatId)
    }
    
    suspend fun deleteMessage(
        chatId: String,
        messageIds: List<String>,
        allMembers: Boolean,
    ) {
        webSource.deleteMessage(
            chatId, messageIds,
            allMembers.compareTo(false)
        )
    }
    
    suspend fun sendMessage(
        chatId: String,
        message: MessageModel,
        attachment: List<AvatarModel>?,
        photos: List<File>?,
        videos: List<File>?,
    ) {
        webSource.sendMessage(
            chatId,
            message.replied?.id,
            message.message?.text,
            photos,
            attachment,
            videos
        )
    }
    
    suspend fun getChat(chatId: String) =
        webSource.getChat(chatId)
    
    suspend fun getMessages(
        chatId: String,
        page: Int? = null,
        perPage: Int? = null,
    ) = webSource.getMessages(chatId, page, perPage)
}