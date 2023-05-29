package ru.rikmasters.gilty.chats.manager

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import ru.rikmasters.gilty.chats.repository.MessageRepository
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import java.io.File

class MessageManager(
    private val store: MessageRepository,
    private val web: ChatWebSource,
): CoroutineController() {
    
    
    private val updateMessageFlow = MutableStateFlow(false)
    
    // список сообщений в потоке собраный пагинатором
    @OptIn(ExperimentalCoroutinesApi::class)
    fun messages(chatId: String) =
        updateMessageFlow.flatMapLatest {
            store.getMessagesPaging(chatId)
        }
    
    // список пишущих в данный момент пользователей в потоке
    val writingFlow = store.writersFlow()
    
    // убрать пользователя из пишущих
    suspend fun deleteWriter(id: String) {
        withContext(IO) {
            store.deleteWriter(id)
        }
    }
    
    suspend fun getChat(chatId: String) =
        withContext(IO) { web.getChat(chatId) }
    
    @Suppress("unused")
    suspend fun getTranslationViewers(
        chatId: String,
        query: String? = null,
        page: Int? = null, perPage: Int? = null,
    ) = web.getTranslationViewers(chatId, query, page, perPage)
    
    suspend fun getTranslationViewersCount(chatId: String) =
        web.getTranslationViewers(chatId, (null), (null), (null))
            .on(
                success = { it.second.total },
                loading = { 0 },
                error = { 0 }
            )
    
    suspend fun completeChat(chatId: String) =
        withContext(IO) { web.completeChat(chatId) }
    
    suspend fun isTyping(chatId: String) =
        withContext(IO) { web.isTyping(chatId) }
    
    suspend fun madeScreenshot(chatId: String) =
        withContext(IO) { web.madeScreenshot(chatId) }
    
    // пометка сообщения или всех как прочитанных
    suspend fun markAsReadMessage(
        chatId: String,
        messageIds: List<String> = emptyList(),
        all: Boolean = false,
    ) = withContext(IO) {
        web.markAsReadMessage(
            chatId, messageIds, all
        )
    }
    
    suspend fun deleteMessage(
        chatId: String,
        messageIds: List<String>,
        allMembers: Boolean,
    ) = withContext(IO) {
        web.deleteMessage(
            chatId = chatId,
            messageIds = messageIds,
            allMembers = allMembers.compareTo(false)
        )
    }
    
    suspend fun sendMessage(
        chatId: String,
        repliedId: String?,
        text: String?,
        attachment: List<AvatarModel>?,
        photos: List<File>?,
    ) = single {
        withContext(IO) {
            web.sendMessage(
                chatId = chatId,
                replyId = repliedId,
                text = text,
                photos = photos,
                attachments = attachment,
            )
        }
    }
}