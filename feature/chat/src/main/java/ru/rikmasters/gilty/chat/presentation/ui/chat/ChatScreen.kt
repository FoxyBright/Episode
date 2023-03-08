package ru.rikmasters.gilty.chat.presentation.ui.chat

import android.Manifest.permission.CAMERA
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.content.FileProvider.getUriForFile
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.REPORTS
import ru.rikmasters.gilty.bottomsheet.viewmodel.BsViewModel
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.ChatAppBarState
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.PinnedBarType.TRANSLATION
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.PinnedBarType.TRANSLATION_AWAIT
import ru.rikmasters.gilty.chat.presentation.ui.chat.bottom.GalleryBs
import ru.rikmasters.gilty.chat.presentation.ui.chat.bottom.HiddenBs
import ru.rikmasters.gilty.chat.viewmodel.ChatViewModel
import ru.rikmasters.gilty.chat.viewmodel.GalleryViewModel
import ru.rikmasters.gilty.chat.viewmodel.HiddenBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.SoftInputAdjust.Nothing
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.report.ReportObjectType.MEETING
import java.io.File
import java.net.URLEncoder.encode

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun ChatScreen(
    vm: ChatViewModel,
    chatId: String,
) {
    val cameraPermissions = rememberPermissionState(CAMERA)
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    // список сообщений чата
    val messages = vm.getMessages(chatId).collectAsLazyPagingItems()
    // состояние меню сообщения
    val messageMenuState by vm.messageMenuState.collectAsState()
    // таймер времени до начала трансляции
    val toTranslation by vm.translationTimer.collectAsState()
    // состояние меню выбора картинки
    val imageMenuState by vm.imageMenuState.collectAsState()
    // состояние меню в шапке
    val kebabMenuState by vm.kebabMenuState.collectAsState()
    // список пользователей в настоящее время пишущих в чат
    val writingUsers by vm.writingUsers.collectAsState()
    // алерт на выход из встречи
    val meetOutAlert by vm.meetOutAlert.collectAsState()
    // кол-во непрочитанных сообщений
    val unreadCount by vm.unreadCount.collectAsState()
    // смотрящие текущюю трансляцию
    val viewers by vm.viewers.collectAsState()
    // выбранное сообщение
    val message by vm.message.collectAsState()
    // тип диалога
    val type by vm.chatType.collectAsState()
    // сообщение в ответе
    val answer by vm.answer.collectAsState()
    // встреча которой принадлежит чат
    val meeting by vm.meet.collectAsState()
    // алерт на жалобу
    val alert by vm.alert.collectAsState()
    // информация о текущем чате
    val chat by vm.chat.collectAsState()
    
    DisposableEffect(Unit) {
        asm.keyboard.setSoftInputMode(Nothing)
        onDispose { asm.keyboard.resetSoftInputAdjust() }
    }
    
    LaunchedEffect(Unit) {
        vm.getChat(chatId)
        vm.getMeet(chat?.meetingId)
        
        if(unreadCount > 0) try {
            listState.scrollToItem(unreadCount)
        } catch(_: Exception) {
            listState.scrollToItem(messages.itemCount)
        }
        
        // TODO реализовать прочтение при просмотре конкретного сообщения
        vm.markAsReadMessage(chatId, all = true)
    }
    
    LaunchedEffect(writingUsers) {
        writingUsers.forEach {
            delay(3000)
            vm.deleteWriter(it.first)
        }
    }
    
    LaunchedEffect(toTranslation) {
        if(type == TRANSLATION_AWAIT) {
            delay(1000)
            vm.timerTick()
        }
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
            ChatState(
                ChatAppBarState(
                    chat.title,
                    meet.organizer.avatar,
                    chat.membersCount,
                    type, viewers,
                    vm.timerConverter(toTranslation),
                    meet.isOnline,
                    (meet.organizer.id == chat.userId)
                ), answer, meet.map(), message,
                messages, chat.userId, alert,
                meetOutAlert, kebabMenuState,
                messageMenuState, imageMenuState,
                listState, unreadCount, writingUsers
            )
        }
    }
    
    Use<ChatViewModel>(LoadingTrait) {
        state?.let { state ->
            ChatContent(
                state, Modifier,
                object: ChatCallback {
                    
                    override fun onAnswerClick(message: MessageModel) {
                        // TODO для навигации к сообщению при клике на ответ
                    }
                    
                    override fun onPinnedBarButtonClick() {
                        scope.launch {
                            if(type == TRANSLATION)
                                vm.toTranslation()
                            else {
                                vm.completeChat(chat)
                                nav.navigate("main")
                            }
                        }
                    }
                    
                    override fun onImageMenuItemSelect(point: Int) {
                        scope.launch {
                            if(point == 1) {
                                if(cameraPermissions.hasPermission)
                                    photographer.launch(uri)
                                else cameraPermissions.launchPermissionRequest()
                            } else {
                                asm.bottomSheet.expand {
                                    when(point) {
                                        0 -> Connector<GalleryViewModel>(vm.scope) {
                                            GalleryBs(it, chat!!.isOnline, chat!!.id)
                                        }
                                        
                                        2 -> Connector<HiddenBsViewModel>(vm.scope) {
                                            HiddenBs(it, chat!!.isOnline, chat!!.id)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    override fun onImageClick(message: MessageModel) {
                        val attach =
                            message.message?.attachments!!.first().file
                        
                        nav.navigate(
                            "photo?type2&image=${encode(attach?.url, "utf-8")}"
                        )
                    }
                    
                    override fun onHiddenClick(message: MessageModel) {
                        val attach =
                            message.message?.attachments?.first()?.file
                        
                        if(attach?.hasAccess != true)
                            scope.launch { vm.onHiddenBlock() }
                        else
                            nav.navigate("photo?type=1&image=${encode(attach.url, ("utf-8"))}")
                    }
                    
                    override fun onMessageMenuItemSelect(
                        point: Int, message: MessageModel,
                    ) {
                        scope.launch {
                            when(point) {
                                0 -> vm.changeAnswer(message)
                                1 -> vm.deleteMessage(chatId, message)
                            }
                            vm.changeMessageMenuState(false)
                        }
                    }
                    
                    override fun onSend() {
                        scope.launch {
                            vm.onSendMessage(chatId, answer?.id, message)
                            listState.animateScrollToItem(0)
                        }
                    }
                    
                    override fun onMenuItemClick(point: Int) {
                        scope.launch {
                            vm.changeKebabMenuState(false)
                            when(point) {
                                0 -> vm.changeMeetOutAlert(true)
                                1 -> asm.bottomSheet.expand {
                                    Connector<BsViewModel> {
                                        BottomSheet(
                                            it, REPORTS,
                                            reportObject = state.meet.id,
                                            reportType = MEETING
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    override fun onTopBarClick() {
                        scope.launch {
                            asm.bottomSheet.expand {
                                Connector<BsViewModel> {
                                    BottomSheet(it, MEET, state.meet.id)
                                }
                            }
                        }
                    }
                    
                    override fun onDownButtonClick() {
                        scope.launch {
                            vm.changeUnreadCount(0)
                            listState.animateScrollToItem(0)
                        }
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
                    
                    override fun onKebabClick() {
                        scope.launch { vm.changeKebabMenuState(!kebabMenuState) }
                    }
                    
                    override fun onListDown() {
                        scope.launch { vm.changeUnreadCount((unreadCount - 1)) }
                    }
                    
                    override fun onMessageMenuDismiss() {
                        scope.launch { vm.changeMessageMenuState(false) }
                    }
                    
                    override fun onImageMenuDismiss() {
                        scope.launch { vm.changeImageMenuState(false) }
                    }
                    
                    override fun onMeetOutAlertDismiss() {
                        scope.launch { vm.changeMeetOutAlert(false) }
                    }
                    
                    override fun closeAlert() {
                        scope.launch { vm.alertDismiss(false) }
                    }
                    
                    override fun onSwipe(message: MessageModel) {
                        scope.launch { vm.changeAnswer(message) }
                    }
                    
                    override fun textChange(text: String) {
                        scope.launch { vm.changeMessage(text) }
                    }
                    
                    override fun onCancelAnswer() {
                        scope.launch { vm.changeAnswer(null) }
                    }
                    
                    override fun onBack() {
                        nav.navigate("main")
                    }
                }
            )
        }
    }
}