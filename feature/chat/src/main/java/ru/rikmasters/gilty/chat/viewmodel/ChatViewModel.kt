package ru.rikmasters.gilty.chat.viewmodel

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.PinnedBarType
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.PinnedBarType.*
import ru.rikmasters.gilty.chats.ChatManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.extentions.FileSource
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.now
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType.ACTIVE
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

class ChatViewModel: ViewModel() {
    
    private val chatManager by inject<ChatManager>()
    private val meetManager by inject<MeetingManager>()
    
    val messages by lazy { chatManager.messageFlow.state(emptyList()) }
    val writingUsers by lazy { chatManager.writingFlow.state(emptyList()) }
    
    private val _chat = MutableStateFlow<ChatModel?>(null)
    val chat = _chat.asStateFlow()
    
    private val _meet = MutableStateFlow<FullMeetingModel?>(null)
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
                chatManager.isTyping(it)
            }
        }
        .state(_message.value, Eagerly)
    
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
        chatManager.deleteWriter(id)
    }
    
    suspend fun markAsReadMessage(
        chatId: String,
        messageIds: List<String> = emptyList(),
        all: Boolean = false,
    ) {
        chatManager.markAsReadMessage(
            chatId, messageIds, all
        )
    }
    
    @Suppress("unused")
    suspend fun madeScreenshot(chatId: String) {
        chatManager.madeScreenshot(chatId)
    }
    
    suspend fun completeChat(chat: ChatModel?) {
        chat?.let {
            chatManager.completeChat(it.id)
            makeToast("Чат для встречи ${it.title} был закрыт закрыт")
        }
    }
    
    private suspend fun getMessages(
        chatId: String,
        forceWeb: Boolean = false,
    ) = singleLoading {
        chatManager.getMessages(chatId, forceWeb)
    }
    
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
            chatManager.deleteMessage(
                chatId, listOf(message.id),
                (it.userId == it.organizer.id)
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
        _chat.emit(chatManager.getChat(chatId))
        
        chat.value?.let {
            
            // кол-во непрочитанных
            changeUnreadCount(it.unreadCount + 1)
            
            // тип прикрепленного топ-бара
            _chatType.emit(
                if(it.organizer.id != it.userId)
                    if(!it.isOnline) MEET
                    else getTranslationType(it.datetime)
                else when {
                    it.meetStatus != ACTIVE -> MEET_FINISHED
                    it.isOnline -> getTranslationType(it.datetime)
                    else -> MEET
                }
            )
            // получение списка сообщений
            getMessages(chatId, false)
        }
    }
    
    private suspend fun getTranslationType(
        date: String?,
    ): PinnedBarType {
        return date?.let {
            val start = of(it).millis()
            val now = now().millis()
            val difference = start - now
            if(now > start && difference < 1_800_000) {
                // получение количества зрителей трансляции
                _viewers.emit(0)
                TRANSLATION
            } else {
                // таймер отсчета до начала трансляции
                _translationTimer.emit(difference)
                TRANSLATION_AWAIT
            }
        } ?: MEET
    }
    
    suspend fun toTranslation() {
        makeToast("Трансляции пока что не доступны на Android-версии Gilty")
    }
    
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
    
    suspend fun onSendMessage(
        chatId: String,
        repliedId: String? = null,
        text: String? = null,
        attachment: List<AvatarModel>? = null,
        photos: List<FileSource>? = null,
        videos: List<FileSource>? = null,
    ) {
        coroutineScope.launch {
            changeAnswer(null)
            clearMessage()
            chatManager.sendMessage(
                chatId, repliedId, text,
                attachment, photos, videos
            )
        }
    }
    
    suspend fun getMeet(meetId: String?) = singleLoading {
        meetId?.let {
            _meet.emit(meetManager.getDetailedMeet(it))
        }
    }
}