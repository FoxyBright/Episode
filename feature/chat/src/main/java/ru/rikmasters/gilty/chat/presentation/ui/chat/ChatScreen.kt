package ru.rikmasters.gilty.chat.presentation.ui.chat

import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.core.content.FileProvider.getUriForFile
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BottomSheet
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.BsType.REPORTS
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.ChatAppBarState
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.PinnedBarType.*
import ru.rikmasters.gilty.chat.presentation.ui.chat.bottom.GalleryBs
import ru.rikmasters.gilty.chat.presentation.ui.chat.bottom.HiddenBs
import ru.rikmasters.gilty.chat.viewmodel.ChatViewModel
import ru.rikmasters.gilty.chat.viewmodel.GalleryViewModel
import ru.rikmasters.gilty.chat.viewmodel.HiddenBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.SoftInputAdjust.Nothing
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.connector.openBS
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.gallery.checkStoragePermission
import ru.rikmasters.gilty.gallery.permissionState
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.LOAD
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.PHOTO
import ru.rikmasters.gilty.shared.common.extentions.*
import ru.rikmasters.gilty.shared.common.extentions.ChatNotificationBlocker.blockNotify
import ru.rikmasters.gilty.shared.common.extentions.ChatNotificationBlocker.clearSelectChat
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.report.ReportObjectType.MEETING
import ru.rikmasters.gilty.translation.bottoms.preview.PreviewBsScreen
import ru.rikmasters.gilty.translation.bottoms.preview.PreviewBsViewModel
import ru.rikmasters.gilty.translation.shared.utils.mediaPermissionState
import java.io.File

@SuppressLint("Recycle")
@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun ChatScreen(
    vm: ChatViewModel,
    chatId: String,
) {
    val listState = rememberLazyListScrollState("chat_$chatId")
    val cameraPermissions = rememberPermissionState(CAMERA)
    val focusManager = LocalFocusManager.current
    val storagePermissions = permissionState()
    
    @Suppress("UNUSED_VARIABLE")
    val mediaPermissions = mediaPermissionState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(CAMERA, RECORD_AUDIO)
    )
    
    vm.changeChatId(chatId)
    
    // список сообщений чата
    val messages = vm.messages.collectAsLazyPagingItems()
    // состояние меню сообщения
    val messageMenuState by vm.messageMenuState.collectAsState()
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
    // время до трансляции
    val remainTime by vm.remainTime.collectAsState()
    // Количество участников
    val membersCount by vm.membersCount.collectAsState()
    
    val viewerSelectImage by vm.viewerSelectImage.collectAsState()
    val viewerImages by vm.viewerImages.collectAsState()
    val photoViewType by vm.viewerType.collectAsState()
    val photoViewState by vm.viewerState.collectAsState()
    
    
    val imeExpandOffset = WindowInsets.ime
        .getBottom(LocalDensity.current)
    LaunchedEffect(imeExpandOffset) {
        if(imeExpandOffset > 0) asm.keyboard
            .setSoftInputMode(Nothing)
    }
    
    LaunchedEffect(Unit) {
        vm.getChat(chatId)
        vm.getMeet(chat?.meetingId)
        if(unreadCount > 0) try {
            listState.scrollToItem(unreadCount)
        } catch(_: Exception) {
            listState.scrollToItem(messages.itemCount)
        }
    }
    
    LaunchedEffect(messages.itemSnapshotList) {
        vm.markAsReadMessage(chatId, all = true)
    }
    
    LaunchedEffect(writingUsers) {
        writingUsers.forEach {
            delay(3000)
            vm.deleteWriter(it.first)
        }
    }
    
    DisposableEffect(chat) {
        chat?.let { blockNotify(context, it.id) }
        onDispose { clearSelectChat(context) }
    }
    
    val uri = getUriForFile(
        context, ("ru.rikmasters.gilty.provider"),
        File(context.filesDir, "my_images")
    )
    
    val photographer =
        rememberLauncherForActivityResult(TakePicture()) { success ->
            if(success) uri?.path?.let {
                scope.launch {
                    val file = File(context.filesDir, "photo.jpg")
                    context.contentResolver
                        .openInputStream(uri)
                        ?.use { file.writeBytes(it.readBytes()) }
                    vm.sendImageMessage(
                        chatId = chatId,
                        photos = listOf(file)
                    )
                    listState.animateScrollToItem(0)
                }
            }
        }
    
    val state = meeting?.let { meet ->
        chat?.let { chat ->
            ChatState(
                topState = ChatAppBarState(
                    name = chat.title,
                    avatar = meet.organizer.avatar,
                    memberCount = membersCount,
                    chatType = type,
                    viewer = viewers,
                    toTranslation = remainTime,
                    isOnline = meet.isOnline,
                    isOrganizer = meet.organizer.id == chat.userId
                ),
                answer = answer,
                meet = meet.map(),
                messageText = message,
                messageList = messages,
                userId = chat.userId,
                alert = alert,
                meetAlert = meetOutAlert,
                kebabMenuState = kebabMenuState,
                messageMenuState = messageMenuState,
                imageMenuState = imageMenuState,
                listState = listState,
                unreadCount = unreadCount,
                writingUsers = writingUsers,
                photoViewState = photoViewState,
                viewerImages = viewerImages,
                viewerSelectImage = viewerSelectImage,
                viewerType = photoViewType
            )
        }
    }
    
    Use<ChatViewModel>(LoadingTrait) {
        state?.let { state ->
            val callback = object: ChatCallback {
                
                override fun onPhotoViewDismiss(state: Boolean) {
                    scope.launch { vm.changePhotoViewState(state) }
                }
                
                override fun onPinnedBarButtonClick() {
                    scope.launch {
                        when(type) {
                            TRANSLATION -> {
                                nav.navigateAbsolute(
                                    "translationviewer/viewer?id=${state.meet.id}"
                                )
                            }
                            TRANSLATION_ORGANIZER -> {
                                if(!permissionsState.allPermissionsGranted) {
                                    permissionsState.launchMultiplePermissionRequest()
                                } else {
                                    scope.launch {
                                        asm.bottomSheet.expand {
                                            PreviewBsScreen(
                                                vm = PreviewBsViewModel(),
                                                closeClicked = {
                                                    scope.launch {
                                                        asm.bottomSheet.collapse()
                                                    }
                                                },
                                                startBroadcastClicked = {
                                                    scope.launch {
                                                        asm.bottomSheet.collapse()
                                                        nav.navigateAbsolute(
                                                            "translations/streamer?id=${state.meet.id}"
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            MEET_FINISHED -> {
                                vm.completeChat(chat)
                                nav.navigate("main")
                            }
                            else -> {}
                        }
                    }
                }
                
                override fun onImageMenuItemSelect(point: Int) {
                    when(point) {
                        1 -> cameraPermissions.let {
                            if(it.hasPermission)
                                photographer.launch(uri)
                            else it.launchPermissionRequest()
                        }
                        
                        0 -> context.checkStoragePermission(
                            storagePermissions, scope, asm,
                        ) {
                            vm.scope
                                .openBS<GalleryViewModel>(scope) {
                                    GalleryBs(
                                        vm = it,
                                        isOnline = chat?.isOnline
                                            ?: false,
                                        chatId = chat?.id ?: ""
                                    )
                                }
                        }
                        2 -> vm.scope
                            .openBS<HiddenBsViewModel>(scope) {
                                HiddenBs(
                                    vm = it,
                                    isOnline = chat?.isOnline
                                        ?: false,
                                    chatId = chat?.id ?: ""
                                )
                            }
                    }
                }
                
                override fun onImageClick(message: MessageModel) {
                    scope.launch {
                        message
                            .message
                            ?.attachments
                            ?.first()
                            ?.file
                            ?.let { attach ->
                                vm.changePhotoViewType(PHOTO)
                                vm.setPhotoViewSelected(attach)
                                vm.setPhotoViewImages(listOf(attach))
                                vm.changePhotoViewState(true)
                            }
                    }
                }
                
                override fun onHiddenClick(message: MessageModel) {
                    val attach = message
                        .message?.attachments?.first()?.file
                    scope.launch {
                        if(attach?.hasAccess == false) {
                            vm.changePhotoViewType(LOAD)
                            vm.setPhotoViewSelected(attach)
                            vm.setPhotoViewImages(listOf(attach))
                            vm.changePhotoViewState(true)
                        } else vm.onHiddenBlock()
                    }
                }
                
                override fun onMessageMenuItemSelect(
                    point: Int,
                    message: MessageModel,
                ) {
                    scope.launch {
                        when(point) {
                            0 -> vm.changeAnswer(message)
                            1 -> vm.deleteMessage(
                                chatId = chatId,
                                message = message
                            )
                        }
                        vm.changeMessageMenuState(false)
                    }
                }
                
                override fun onSend() {
                    scope.launch {
                        vm.onSendMessage(
                            chatId = chatId,
                            replied = answer?.id,
                            text = message
                        )
                        listState.animateScrollToItem(0)
                    }
                }
                
                override fun onMenuItemClick(point: Int) {
                    scope.launch {
                        vm.changeKebabMenuState(false)
                        when(point) {
                            0 -> vm.changeMeetOutAlert(true)
                            1 -> asm.bottomSheet.expand {
                                BottomSheet(
                                    scope = vm.scope,
                                    type = REPORTS,
                                    reportObject = state.meet.id,
                                    reportType = MEETING
                                )
                            }
                        }
                    }
                }
                
                override fun onTopBarClick() {
                    scope.launch {
                        asm.bottomSheet.expand {
                            BottomSheet(
                                scope = vm.scope,
                                type = MEET,
                                meetId = state.meet.id
                            )
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
                    scope.launch {
                        vm.changeKebabMenuState(!kebabMenuState)
                    }
                }
                
                override fun onListDown() {
                    scope.launch {
                        vm.changeUnreadCount((unreadCount - 1))
                    }
                }
                
                override fun onMessageMenuDismiss() {
                    scope.launch {
                        vm.changeMessageMenuState(false)
                    }
                }
                
                override fun onImageMenuDismiss() {
                    scope.launch {
                        vm.changeImageMenuState(false)
                    }
                }
                
                override fun onMeetOutAlertDismiss() {
                    scope.launch {
                        vm.changeMeetOutAlert(false)
                    }
                }
                
                override fun closeAlert() {
                    scope.launch {
                        vm.alertDismiss(false)
                    }
                }
                
                override fun onSwipe(
                    message: MessageModel,
                ) {
                    scope.launch {
                        vm.changeAnswer(message)
                    }
                }
                
                override fun textChange(text: String) {
                    scope.launch {
                        vm.changeMessage(text)
                    }
                }
                
                override fun onCancelAnswer() {
                    scope.launch {
                        vm.changeAnswer(null)
                    }
                }
                
                override fun onBack() {
                    nav.navigate("main")
                }
            }
            
            ChatContent(
                state = state,
                callback = callback
            )
        }
    }
}