package ru.rikmasters.gilty.chat.presentation.ui.dialog

import android.widget.Toast
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.Chat.logD
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bars.ChatAppBarState
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bars.PinnedBarType.TRANSLATION
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bottom.HiddenBs
import ru.rikmasters.gilty.chat.viewmodel.DialogViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.SoftInputAdjust.Nothing
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.shared.common.extentions.FULL_DATE_FORMAT_WIDTH_ZONE
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.chat.MemberMessageModel
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.MESSAGE
import java.util.UUID

@Composable
fun DialogScreen(
    vm: DialogViewModel,
    chatId: String,
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    var answer by
    remember { mutableStateOf<MessageModel?>(null) }
    
    var alert by
    remember { mutableStateOf(false) }
    var meetOutAlert by
    remember { mutableStateOf(false) }
    var menuState by
    remember { mutableStateOf(false) }
    var kebabMenuState by
    remember { mutableStateOf(false) }
    var messageMenuState by
    remember { mutableStateOf(false) }
    var imageMenuState by
    remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    
    fun listToDown() {
        scope.launch {
            listState.animateScrollToItem(0)
        }
    }
    
    val messageList by vm.messageList.collectAsState()
    val toTranslation by vm.translationTimer.collectAsState()
    val meeting by vm.meet.collectAsState()
    val unreadCount by vm.unreadCount.collectAsState()
    val type by vm.dialogType.collectAsState()
    val message by vm.message.collectAsState()
    val dialog by vm.chat.collectAsState()
    val viewers by vm.viewers.collectAsState()
    val user by vm.user.collectAsState()
    
    val title = meeting
        ?.tags
        ?.joinToString((", "))
        { it.title }
        ?: ""
    
    fun Long.toTime(): String? {
        if(this == 0L) {
            scope.launch {
                vm.changeDialogType(TRANSLATION)
            }
            return null
        }
        val min = this / 60
        val sec = this - 60 * min
        return "${
            if(min < 10) "0" else ""
        }$min:${
            if(sec < 10) "0" else ""
        }$sec"
    }
    
    DisposableEffect(Unit) {
        asm.keyboard.setSoftInputMode(Nothing)
        onDispose { asm.keyboard.resetSoftInputAdjust() }
    }
    
    LaunchedEffect(Unit) {
        vm.getChat(chatId)
        vm.getMeet(dialog?.meetingId)
        vm.getUser()
        listState.scrollToItem(unreadCount)
    }
    
    val state = meeting?.let { meet ->
        dialog?.let { chat ->
            user?.let { user ->
                DialogState(
                    ChatAppBarState(
                        title,
                        meet.organizer.avatar,
                        chat.membersCount,
                        type, viewers,
                        toTranslation?.toTime(),
                        meet.isOnline,
                        (meet.organizer.id == user.id)
                    ), answer, meet.map(), message,
                    messageList, user, alert,
                    meetOutAlert, kebabMenuState,
                    messageMenuState, imageMenuState,
                    listState, unreadCount
                )
            }
        }
    }
    val context = LocalContext.current
    Use<DialogViewModel>(LoadingTrait) {
        state?.let { state ->
            DialogContent(
                state, Modifier,
                object: DialogCallback {
                    override fun onSwipeToRefresh() {
                        Toast.makeText(context, "SwipeRefresh", Toast.LENGTH_SHORT).show()
                    }
    
                    override fun onAnswerClick(message: MessageModel) {
                        scope.launch {
                            listState.animateScrollToItem(
                                messageList.indexOf(message)
                            )
                        }
                    }
                    
                    override fun onBack() {
                        nav.navigate("main")
                    }
                    
                    override fun onImageMenuDismiss() {
                        imageMenuState = false
                    }
                    
                    override fun onImageMenuItemSelect(point: Int) {
                        scope.launch {
                            vm.onAttachmentMenuSelect(point)
                            asm.bottomSheet.expand {
                                when(point) {
                                    0, 1 -> {}
                                    2 -> HiddenBs()
                                }
                            }
                        }
                    }
                    
                    override fun onPinnedBarButtonClick() {
                        scope.launch {
                            if(type == TRANSLATION)
                                vm.toTranslation()
                            else {
                                vm.finishedChat(title)
                                nav.navigate("main")
                            }
                        }
                    }
                    
                    override fun onImageClick(message: MessageModel) {
                        logD(message.message?.attachments!!.first().file.url)
                        nav.navigate(
                            "photo?type2&image=${
                                message.message?.attachments!!.first().file.url
                            }"
                        )
                    }
                    
                    override fun onDownButtonClick() {
                        scope.launch {
                            listToDown()
                            vm.changeUnreadCount(0)
                        }
                    }
                    
                    override fun onHiddenClick(message: MessageModel) {
                        scope.launch {
                            if(message.message?.attachments
                                    ?.first()?.file?.hasAccess != true
                            ) vm.onHiddenClick()
                            else nav.navigate(
                                "photo?type=1&image=${
                                    message.message?.attachments!!.first().file.url
                                }"
                            )
                        }
                    }
                    
                    override fun onListDown() {
                        scope.launch {
                            vm.changeUnreadCount(unreadCount - 1)
                        }
                    }
                    
                    override fun onSwipe(message: MessageModel) {
                        answer = message
                    }
                    
                    override fun onMessageMenuDismiss() {
                        messageMenuState = false
                    }
                    
                    override fun onMessageMenuItemSelect(
                        point: Int,
                        message: MessageModel,
                    ) {
                        scope.launch {
                            when(point) {
                                0 -> answer = message
                                1 -> {
                                    answer = null
                                    vm.deleteMessage(
                                        chatId, listOf(message.id), true
                                    )
                                }
                            }
                        }
                        messageMenuState = false
                    }
                    
                    override fun textChange(text: String) {
                        scope.launch { vm.changeMessage(text) }
                    }
                    
                    override fun gallery() {
                        focusManager.clearFocus()
                        imageMenuState = true
                    }
                    
                    override fun onMeetOut() {
                        meetOutAlert = false
                        nav.navigate("main")
                    }
                    
                    override fun onMeetOutAlertDismiss() {
                        meetOutAlert = false
                    }
                    
                    override fun onSend() {
                        scope.launch {
                            vm.onSendMessage(
                                chatId, MessageModel(
                                    id = UUID.randomUUID().toString(),
                                    type = MESSAGE,
                                    replied = answer,
                                    notification = null,
                                    message = MemberMessageModel(
                                        author = state.user,
                                        text = message,
                                        attachments = null,
                                        is_author = true
                                    ),
                                    otherRead = false,
                                    isRead = false,
                                    isDelivered = true,
                                    createdAt = LocalDateTime
                                        .now()
                                        .minusHour(3)
                                        .format(FULL_DATE_FORMAT_WIDTH_ZONE)
                                )
                            )
                            answer = null
                            vm.clearMessage()
                            vm.getMessages(chatId)
                            listToDown()
                        }
                    }
                    
                    override fun onCancelAnswer() {
                        answer = null
                    }
                    
                    override fun closeAlert() {
                        alert = false
                    }
                    
                    override fun onTopBarClick() {
                    }
                    
                    override fun onMenuItemClick(point: Int) {
                        kebabMenuState = false
                        when(point) {
                            0 -> meetOutAlert = true
                            1 -> scope.launch {
                                asm.bottomSheet.expand {
                                    menuState = false
                                    //                            ComplainsContent(meet?.id) {
                                    //                                scope.launch {
                                    //                                    asm.bottomSheet.collapse()
                                    //                                }; alert = true
                                    //                            }
                                }
                            }
                        }
                    }
                    
                    override fun onKebabClick() {
                        kebabMenuState = !kebabMenuState
                    }
                }
            )
        }
    }
}
