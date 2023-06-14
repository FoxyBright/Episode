package ru.rikmasters.gilty.chat.viewmodel

import android.content.Context
import androidx.paging.cachedIn
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import org.koin.core.component.inject
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.PinnedBarType.*
import ru.rikmasters.gilty.chats.manager.MessageManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.PHOTO
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.compressor.compress
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.nowZ
import ru.rikmasters.gilty.shared.common.extentions.LocalTime.Companion.ofZ
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType.ACTIVE
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.translations.repository.TranslationRepository
import java.io.File

class ChatViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    private val messageManager by inject<MessageManager>()
    private val translationRepository by inject<TranslationRepository>()
    
    val writingUsers by lazy {
        messageManager.writingFlow.state(emptyList())
    }
    
    private val context = getKoin().get<Context>()
    
    private val _chatId = MutableStateFlow("")
    
    private val _chat =
        MutableStateFlow<ChatModel?>(null)
    val chat = _chat.asStateFlow()
    
    private val _meet =
        MutableStateFlow<FullMeetingModel?>(null)
    val meet = _meet.asStateFlow()
    
    private val _chatType = MutableStateFlow(MEET)
    val chatType = _chatType.asStateFlow()
    
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()
    
    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val messageDebounced = message
        .debounce(250)
        .onEach {
            chat.value?.id?.let {
                messageManager.isTyping(it).on(
                    success = {},
                    loading = {},
                    error = {}
                )
            }
        }
        .state(_message.value, Eagerly)
    
    private val _translationInfoModel =
        MutableStateFlow<TranslationInfoModel?>(null)
    
    private val _answer = MutableStateFlow<MessageModel?>(null)
    val answer = _answer.asStateFlow()
    
    private val _viewers = MutableStateFlow<Int?>(null)
    val viewers = _viewers.asStateFlow()
    
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()
    
    private val _translationTimer = MutableStateFlow<Long?>(null)
    val translationTimer = _translationTimer.asStateFlow()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _meetOutAlert = MutableStateFlow(false)
    val meetOutAlert = _meetOutAlert.asStateFlow()
    
    private val _kebabMenuState = MutableStateFlow(false)
    val kebabMenuState = _kebabMenuState.asStateFlow()
    
    private val _messageMenuState = MutableStateFlow(false)
    val messageMenuState = _messageMenuState.asStateFlow()
    
    private val _imageMenuState = MutableStateFlow(false)
    val imageMenuState = _imageMenuState.asStateFlow()
    
    private val _viewerState = MutableStateFlow(false)
    val viewerState = _viewerState.asStateFlow()
    
    private val _viewerImages = MutableStateFlow(emptyList<AvatarModel?>())
    val viewerImages = _viewerImages.asStateFlow()
    
    private val _viewerType = MutableStateFlow(PHOTO)
    val viewerType = _viewerType.asStateFlow()
    
    private val _viewerSelectImage = MutableStateFlow<AvatarModel?>(null)
    val viewerSelectImage = _viewerSelectImage.asStateFlow()
    
    suspend fun changePhotoViewState(state: Boolean) {
        _viewerState.emit(state)
    }
    
    suspend fun changePhotoViewType(type: PhotoViewType) {
        _viewerType.emit(type)
    }
    
    suspend fun setPhotoViewImages(list: List<AvatarModel?>) {
        _viewerImages.emit(list)
    }
    
    suspend fun setPhotoViewSelected(photo: AvatarModel?) {
        _viewerSelectImage.emit(photo)
    }
    
    suspend fun changeMeetOutAlert(state: Boolean) {
        _meetOutAlert.emit(state)
    }
    
    suspend fun changeKebabMenuState(state: Boolean) {
        _kebabMenuState.emit(state)
    }
    
    suspend fun changeMessageMenuState(state: Boolean) {
        _messageMenuState.emit(state)
    }
    
    suspend fun changeImageMenuState(state: Boolean) {
        _imageMenuState.emit(state)
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun deleteWriter(id: String) {
        messageManager.deleteWriter(id)
    }
    
    suspend fun markAsReadMessage(
        chatId: String,
        messageIds: List<String> = emptyList(),
        all: Boolean = false,
    ) {
        messageManager.markAsReadMessage(
            chatId, messageIds, all
        ).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    @Suppress("unused")
    suspend fun madeScreenshot(chatId: String) {
        messageManager.madeScreenshot(chatId).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun completeChat(chat: ChatModel?) {
        chat?.let { c ->
            messageManager.completeChat(c.id).on(
                success = {
                    makeToast(
                        "Чат для встречи ${
                            c.title
                        } был закрыт закрыт"
                    )
                },
                loading = {},
                error = {
                    context.errorToast(
                        it.serverMessage
                    )
                }
            )
        }
    }
    
    fun changeChatId(chatId: String) {
        _chatId.value = chatId
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val messages = _chatId.flatMapLatest { chatId ->
        messageManager.messages(chatId)
    }.cachedIn(coroutineScope)
    
    suspend fun changeAnswer(message: MessageModel?) {
        _answer.emit(message)
    }
    
    suspend fun deleteMessage(
        chatId: String,
        message: MessageModel,
    ) = singleLoading {
        if(message == answer.value)
            _answer.emit(null)
        chat.value?.let {
            messageManager.deleteMessage(
                chatId, listOf(message.id),
                (it.userId == it.organizer.id)
            ).on(
                success = {},
                loading = {},
                error = { e ->
                    context.errorToast(
                        e.serverMessage
                    )
                }
            )
        }
    }
    
    suspend fun changeUnreadCount(state: Int) {
        _unreadCount.emit(state)
    }
    
    suspend fun onHiddenBlock() {
        makeToast("Скрытое фото больше недоступно к просмотру")
    }
    
    suspend fun getChat(chatId: String) = singleLoading {
        messageManager.getChat(chatId).on(
            success = { res ->
                _chat.emit(res)
                chat.value?.let { c ->
                    changeUnreadCount(c.unreadCount + 1)
                    _chatType.emit(
                        if(c.organizer.id != c.userId)
                            if(!c.isOnline) MEET
                            else getTranslationType(
                                c.datetime, chatId, false
                            )
                        else when {
                            c.meetStatus != ACTIVE -> MEET_FINISHED
                            c.isOnline -> getTranslationType(
                                c.datetime, chatId, true
                            )
                            else -> MEET
                        }
                    )
                }
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    private suspend fun getTranslationType(
        date: String?,
        @Suppress("UNUSED_PARAMETER")
        chatId: String,
        isOrganizer: Boolean,
    ) = date?.let {
        val start = ofZ(it).millis()
        val now = nowZ().millis()
        val difference = start - now
        
        logD("Data --->>> $date")
        logD("Data --->>> ${ofZ(date)}")
        logD("Data --->>> ${nowZ()}")
        logD("Data --->>> ${ofZ(it).millis() - nowZ().millis()}")
        logD("Data --->>> ${(ofZ(it).millis() - nowZ().millis()) / 3600000}")
        
        when {
            (now > start) -> {
                _translationInfoModel.value?.id?.let { id ->
                    _viewers.emit(
                        messageManager
                            .getTranslationViewersCount(id)
                    )
                }
                if(isOrganizer)
                    TRANSLATION_ORGANIZER
                else TRANSLATION
            }
            
            (start - now) < 1_800_000 -> {
                _translationTimer.emit(difference)
                if(isOrganizer) {
                    TRANSLATION_ORGANIZER_AWAIT
                } else {
                    TRANSLATION_AWAIT
                }
            }
            
            else -> MEET
        }
    } ?: MEET
    
    fun timerConverter(millis: Long?): String? {
        millis?.let {
            val seconds = it / 1000
            if(seconds > 3599) return "${seconds / 3600} ч"
            if(seconds > 0L) {
                val min = seconds / 60
                val sec = seconds - 60 * min
                return "${
                    if(min < 10) "0" else ""
                }$min:${
                    if(sec < 10) "0" else ""
                }$sec"
            } else {
                coroutineScope.launch {
                    _chatType.emit(TRANSLATION)
                }
                return null
            }
        } ?: return null
    }
    
    suspend fun timerTick() {
        translationTimer.value?.let {
            _translationTimer.emit(
                it.minus(1)
            )
        }
    }
    
    suspend fun changeMessage(text: String) {
        _message.emit(text)
    }
    
    private suspend fun clearMessage() {
        _message.emit("")
    }
    
    suspend fun sendImageMessage(
        chatId: String,
        photos: List<File>,
    ) = singleLoading {
        onSendMessage(
            chatId = chatId,
            photos = photos
        )
    }
    
    suspend fun sendHiddenMessage(
        chatId: String,
        attachment: List<AvatarModel>? = null,
    ) {
        onSendMessage(
            chatId = chatId,
            attachment = attachment
        )
    }
    
    suspend fun onSendMessage(
        chatId: String,
        replied: String? = null,
        text: String? = null,
        attachment: List<AvatarModel>? = null,
        photos: List<File>? = null,
    ) {
        changeAnswer(null)
        clearMessage()
        messageManager.sendMessage(
            chatId = chatId,
            repliedId = replied,
            text = text,
            attachment = attachment,
            photos = photos?.map {
                it.compress(context)
            }
        ).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun getMeet(meetId: String?) = singleLoading {
        meetId?.let { meetId ->
            meetManager.getDetailedMeet(meetId).on(
                success = { meeting ->
                    _meet.emit(meeting)
                    coroutineScope.launch {
                        translationRepository.getTranslationInfo(
                            translationId = meetId
                        ).onSuccess {
                            _translationInfoModel.emit(it)
                            translationRepository.connectWebSocket(
                                translationId = it.id,
                                userId = it.userId
                            )
                        }
                    }
                },
                loading = {},
                error = {
                    context.errorToast(
                        it.serverMessage
                    )
                }
            )
        }
    }
}