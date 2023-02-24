package ru.rikmasters.gilty.chat.presentation.ui.dialog

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.content.FileProvider.getUriForFile
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
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
import ru.rikmasters.gilty.shared.common.extentions.InputStreamSource
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.chat.MemberMessageModel
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.MESSAGE
import java.io.File
import java.net.URLEncoder.encode
import java.util.UUID

@Composable
@SuppressLint("Recycle")
@OptIn(ExperimentalPermissionsApi::class)
fun DialogScreen(
    vm: DialogViewModel,
    chatId: String,
) {
    val cameraPermissions = rememberPermissionState(CAMERA)
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    // алерт на жалобу
    val alert by vm.alert.collectAsState()
    // алерт на выход из встречи
    val meetOutAlert by vm.meetOutAlert.collectAsState()
    // состояние меню в шапке
    val kebabMenuState by vm.kebabMenuState.collectAsState()
    // состояние меню сообщения
    val messageMenuState by vm.messageMenuState.collectAsState()
    // состояние меню выбора картинки
    val imageMenuState by vm.imageMenuState.collectAsState()
    // таймер времени до начала трансляции
    val toTranslation by vm.translationTimer.collectAsState()
    // список сообщений чата
    val messages by vm.messages.collectAsState()
    // список пользователей в настоящее время пишущих в чат
    val writingUsers by vm.writingUsers.collectAsState()
    // тип диалога (для определения прикрепленного бара)
    val type by vm.dialogType.collectAsState()
    // кол-во непрочитанных сообщений
    val unreadCount by vm.unreadCount.collectAsState()
    // встреча которой принадлежит чат
    val meeting by vm.meet.collectAsState()
    // сообщение в ответе
    val answer by vm.answer.collectAsState()
    // выбранное сообщение
    val message by vm.message.collectAsState()
    // информация о текущем чате
    val chat by vm.chat.collectAsState()
    // смотрящие текущюю трансляцию
    val viewers by vm.viewers.collectAsState()
    // текущий пользователь
    val user by vm.user.collectAsState()
    
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
        vm.getMeet(chat?.meetingId)
        vm.getUser()
        listState.scrollToItem(unreadCount)
        vm.markAsReadMessage(chatId, all = true)
    }
    
    val uri = getUriForFile(
        context, ("ru.rikmasters.gilty.provider"),
        File(context.filesDir, "my_images")
    )
    
    val photographer =
        rememberLauncherForActivityResult(TakePicture()) { success ->
            if(success) context.contentResolver.openInputStream(uri)?.let {
                scope.launch {
                    vm.onSendMessage(chatId, photos = listOf(InputStreamSource(it)))
                    listState.animateScrollToItem(0)
                }
            }
        }
    
    val state = meeting?.let { meet ->
        chat?.let { chat ->
            user?.let { user ->
                DialogState(
                    ChatAppBarState(
                        chat.title,
                        meet.organizer.avatar,
                        chat.membersCount,
                        type, viewers,
                        toTranslation?.toTime(),
                        meet.isOnline,
                        (meet.organizer.id == user.id)
                    ), answer, meet.map(), message,
                    messages, user, alert,
                    meetOutAlert, kebabMenuState,
                    messageMenuState, imageMenuState,
                    listState, unreadCount, writingUsers
                )
            }
        }
    }
    
    LaunchedEffect(writingUsers) {
        writingUsers.forEach {
            delay(3000)
            vm.deleteWriter(it.first)
        }
    }
    
    Use<DialogViewModel>(LoadingTrait) {
        state?.let { state ->
            DialogContent(
                state, Modifier,
                object: DialogCallback {
                    
                    override fun onAnswerClick(message: MessageModel) {
                        scope.launch {
                            try {
                                listState.animateScrollToItem(
                                    messages.indexOf(message)
                                )
                            } catch(e: Exception) {
                                e.stackTraceToString()
                            }
                        }
                    }
                    
                    override fun onBack() {
                        nav.navigate("main")
                    }
                    
                    
                    override fun onImageMenuDismiss() {
                        scope.launch { vm.changeImageMenuState(false) }
                    }
                    
                    override fun onPinnedBarButtonClick() {
                        scope.launch {
                            if(type == TRANSLATION)
                                vm.toTranslation()
                            else {
                                vm.finishedChat(state.topState.name)
                                nav.navigate("main")
                            }
                        }
                    }
                    
                    @SuppressLint("PermissionLaunchedDuringComposition")
                    override fun onImageMenuItemSelect(point: Int) {
                        scope.launch {
                            when(point) {
                                0 -> {}
                                1 -> if(cameraPermissions.hasPermission)
                                    photographer.launch(uri)
                                else
                                    cameraPermissions.launchPermissionRequest()
                                
                                2 -> asm.bottomSheet.expand { HiddenBs() }
                            }
                        }
                    }
                    
                    override fun onImageClick(message: MessageModel) {
                        val attach =
                            message.message?.attachments!!.first().file
                        
                        nav.navigate(
                            "photo?type2&image=${encode(attach.url, "utf-8")}"
                        )
                    }
                    
                    override fun onDownButtonClick() {
                        scope.launch {
                            vm.changeUnreadCount(0)
                            listState.animateScrollToItem(0)
                        }
                    }
                    
                    override fun onHiddenClick(message: MessageModel) {
                        val attach =
                            message.message?.attachments?.first()?.file
                        
                        if(attach?.hasAccess != true)
                            scope.launch { vm.onHiddenBlock() }
                        else
                            nav.navigate(
                                "photo?type=1&image=${encode(attach.url, "utf-8")}"
                            )
                    }
                    
                    override fun onListDown() {
                        scope.launch {
                            vm.changeUnreadCount(unreadCount - 1)
                        }
                    }
                    
                    override fun onSwipe(message: MessageModel) {
                        scope.launch { vm.changeAnswer(message) }
                    }
                    
                    override fun onMessageMenuDismiss() {
                        scope.launch { vm.changeMessageMenuState(false) }
                    }
                    
                    override fun onMessageMenuItemSelect(
                        point: Int,
                        message: MessageModel,
                    ) {
                        scope.launch {
                            when(point) {
                                0 -> vm.changeAnswer(message)
                                1 -> vm.deleteMessage(chatId, message)
                            }
                            vm.changeMessageMenuState(false)
                        }
                    }
                    
                    override fun textChange(text: String) {
                        scope.launch { vm.changeMessage(text) }
                    }
                    
                    override fun gallery() {
                        scope.launch {
                            focusManager.clearFocus()
                            vm.changeImageMenuState(true)
                        }
                    }
                    
                    override fun onMeetOut() {
                        scope.launch {
                            vm.changeMeetOutAlert(false)
                            nav.navigate("main")
                        }
                    }
                    
                    override fun onMeetOutAlertDismiss() {
                        scope.launch { vm.changeMeetOutAlert(false) }
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
                            listState.animateScrollToItem(0)
                        }
                    }
                    
                    override fun onCancelAnswer() {
                        scope.launch { vm.changeAnswer(null) }
                    }
                    
                    override fun closeAlert() {
                        scope.launch { vm.alertDismiss(false) }
                    }
                    
                    override fun onTopBarClick() {
                    }
                    
                    override fun onMenuItemClick(point: Int) {
                        scope.launch {
                            vm.changeKebabMenuState(false)
                            when(point) {
                                0 -> vm.changeMeetOutAlert(true)
                                1 -> asm.bottomSheet.expand {
                                    //  ComplainsContent(meet?.id) {
                                    //      scope.launch {
                                    //          asm.bottomSheet.collapse()
                                    //      }; alert = true
                                    //  }
                                }
                            }
                        }
                    }
                    
                    override fun onKebabClick() {
                        scope.launch {
                            vm.changeKebabMenuState(!kebabMenuState)
                        }
                    }
                }
            )
        }
    }
}