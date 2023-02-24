package ru.rikmasters.gilty.chats

import ru.rikmasters.gilty.chats.repository.ChatRepository
import ru.rikmasters.gilty.chats.repository.MessageRepository
import ru.rikmasters.gilty.chats.websocket.WebSocketHandler
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.core.viewmodel.Strategy.JOIN
import ru.rikmasters.gilty.shared.common.extentions.FileSource
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

class ChatManager(
    
    private val chatRepository: ChatRepository,
    
    private val messageRepository: MessageRepository,
    
    private val webSocket: WebSocketHandler,
    
    private val webSource: ChatWebSource,
): CoroutineController() {
    
    val chatsFlow = chatRepository.chatsFlow()
    val messageFlow = messageRepository.messageFlow()
    val writingFlow = messageRepository.writersFlow()
    
    suspend fun deleteWriter(id: String) {
        messageRepository.deleteWriter(id)
    }
    
    suspend fun getMessages(
        chatId: String, forceWeb: Boolean,
    ) {
        messageRepository.getMessages(chatId, forceWeb)
    }
    
    suspend fun connectToChat(chatId: String) {
        webSocket.connectToChat(chatId)
    }
    
    suspend fun getDialogs(
        forceWeb: Boolean,
    ) = chatRepository.getChats(forceWeb)
    
    suspend fun deleteDialog(
        chatId: String,
        forAll: Boolean,
    ) {
        webSource.deleteDialog(chatId, forAll)
    }
    
    suspend fun connect(userId: String) = single(JOIN) {
        webSocket.connect(userId)
    }
    
    @Suppress("unused")
    suspend fun getChatsStatus() = webSource.getChatsStatus()
    
    @Suppress("unused")
    suspend fun unmuteChatNotifications(chatId: String) {
        webSource.unmuteChatNotifications(chatId)
    }
    
    @Suppress("unused")
    suspend fun muteChatNotifications(
        chatId: String, unmuteAt: String,
    ) {
        webSource.muteChatNotifications(chatId, unmuteAt)
    }
    
    @Suppress("unused")
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
    
    suspend fun markAsReadMessage(
        chatId: String,
        messageIds: List<String> = emptyList(),
        all: Boolean = false,
    ) {
        webSource.markAsReadMessage(
            chatId, messageIds, all
        )
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
        message: MessageModel?,
        attachment: List<AvatarModel>?,
        photos: List<FileSource>?,
        videos: List<FileSource>?,
    ) {
        webSource.sendMessage(
            chatId,
            message?.replied?.id,
            message?.message?.text,
            photos?.map { it.bytes() },
            attachment,
            videos?.map { it.bytes() }
        )
    }
    
    suspend fun getChat(chatId: String) =
        webSource.getChat(chatId)
}