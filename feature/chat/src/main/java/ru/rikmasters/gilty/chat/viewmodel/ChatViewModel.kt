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
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.extentions.FileSource
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.now
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType.ACTIVE
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

class ChatViewModel: ViewModel() {
    
    private val chatManager by inject<ChatManager>()
    private val meetManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()
    
    val messages by lazy { chatManager.messageFlow.state(emptyList()) }
    val writingUsers by lazy { chatManager.writingFlow.state(emptyList()) }
    
    private val _chat = MutableStateFlow<ChatModel?>(null)
    val chat = _chat.asStateFlow()
    
    private val _meet = MutableStateFlow<FullMeetingModel?>(null)
    val meet = _meet.asStateFlow()
    
    private val _chatType = MutableStateFlow(MEET)
    val chatType = _chatType.asStateFlow()
    
    private val _user = MutableStateFlow<UserModel?>(null)
    val user = _user.asStateFlow()
    
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
    
    @Suppress("unused")
    suspend fun completeChat(chatId: String) {
        chatManager.completeChat(chatId)
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
        
        chatManager.deleteMessage(
            chatId, listOf(message.id),
            (user.value?.id == chat.value?.organizer?.id)
        )
    }
    
    suspend fun changeUnreadCount(state: Int) {
        _unreadCount.emit(state)
    }
    
    suspend fun onHiddenBlock() {
        makeToast("Скрытое фото больше недоступно к просмотру")
    }
    
    suspend fun getChat(chatId: String) = singleLoading {
        val chat = chatManager.getChat(chatId)
        _chat.emit(chat)
        changeUnreadCount(chat.unreadCount + 1)
        _chatType.emit(
            when {
                chat.isOnline && chat.meetStatus == ACTIVE
                -> {
                    val translationStart = of(chat.datetime).millis()
                    
                    val now = now().millis()
                    
                    if(now < translationStart) TRANSLATION else {
                        _translationTimer.emit((now - translationStart))
                        TRANSLATION_AWAIT
                    }
                }
                
                else -> MEET_FINISHED
            }
        )
        _viewers.emit(
            if(chatType.value == TRANSLATION)
                0 else null //TODO сюда кол-во смотрящих трансляцию
        )
        getMessages(chatId, false)
    }
    
    suspend fun finishedChat(title: String) {
        makeToast("Чат для встречи $title закрыт")
    }
    
    suspend fun toTranslation() {
        makeToast("Трансляции пока что не доступны")
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
                    changeChatType(TRANSLATION)
                }
                return null
            }
        } ?: return null
    }
    
    @Suppress("unused")
    suspend fun timerTick() {
        translationTimer.value?.let {
            _translationTimer.emit(
                it.minus(1)
            )
        }
    }
    
    private suspend fun changeChatType(
        type: PinnedBarType,
    ) {
        _chatType.emit(type)
    }
    
    suspend fun getUser() = singleLoading {
        _user.emit(profileManager.getProfile().map())
    }
    
    suspend fun changeMessage(text: String) {
        _message.emit(text)
    }
    
    private suspend fun clearMessage() {
        _message.emit("")
    }
    
    suspend fun onSendMessage(
        chatId: String,
        message: MessageModel? = null,
        attachment: List<AvatarModel>? = null,
        photos: List<FileSource>? = null,
        videos: List<FileSource>? = null,
    ) {
        changeAnswer(null)
        clearMessage()
        chatManager.sendMessage(
            chatId, message, attachment,
            photos, videos
        )
    }
    
    suspend fun getMeet(meetId: String?) = singleLoading {
        meetId?.let {
            _meet.emit(meetManager.getDetailedMeet(it))
        }
    }
}