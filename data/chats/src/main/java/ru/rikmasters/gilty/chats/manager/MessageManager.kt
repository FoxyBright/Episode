package ru.rikmasters.gilty.chats.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import ru.rikmasters.gilty.chats.repository.MessageRepository
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.chats.source.websocket.WebSocketHandler
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.shared.common.extentions.FileSource
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

class MessageManager(
    
    private val messageRepository: MessageRepository,
    
    private val webSource: ChatWebSource,

    private val socket: WebSocketHandler
): CoroutineController() {


    private val updateMessageFlow = MutableStateFlow(false)
    
    // список сообщений в потоке собраный пагинатором
    @OptIn(ExperimentalCoroutinesApi::class)
    fun messages(chatId: String, scope: CoroutineScope) = updateMessageFlow.flatMapLatest {
        messageRepository.getMessagesPaging(chatId)
    }

    // список пишущих в данный момент пользователей в потоке
    val writingFlow = messageRepository.writersFlow()
    
    // убрать пользователя из пишущих
    suspend fun deleteWriter(id: String) {
        messageRepository.deleteWriter(id)
    }
    
    // получение подробных данных о конкретном чате
    suspend fun getChat(chatId: String) =
        webSource.getChat(chatId)
    
    // завершение чата
    suspend fun completeChat(chatId: String) {
        webSource.completeChat(chatId)
    }
    
    // пользователь печатает
    suspend fun isTyping(chatId: String) {
        webSource.isTyping(chatId)
    }
    
    // был сделан скриншот
    suspend fun madeScreenshot(chatId: String) {
        webSource.madeScreenshot(chatId)
    }
    
    // пометка сообщения или всех как прочитанных
    suspend fun markAsReadMessage(
        chatId: String,
        messageIds: List<String> = emptyList(),
        all: Boolean = false,
    ) {
        webSource.markAsReadMessage(
            chatId, messageIds, all
        )
    }
    
    // удаление сообщения
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
    
    // отправка сообщения
    suspend fun sendMessage(
        chatId: String,
        repliedId: String?,
        text: String?,
        attachment: List<AvatarModel>?,
        photos: List<FileSource>?,
        videos: List<FileSource>?,
    ) {
        webSource.sendMessage(
            chatId, repliedId, text,
            photos?.map { it.bytes() },
            attachment,
            videos?.map { it.bytes() }
        )
    }
}