package ru.rikmasters.gilty.chat.presentation.ui.chat

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.ui.chat.bottom.HiddenPhotoBottomSheet
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.presentation.ui.user.organizer
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBSCallback
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBSState
import ru.rikmasters.gilty.shared.common.meetBS.MeetingBottomSheet
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.chat.MessageType.MESSAGE
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel

@Composable
fun ChatScreen(nav: NavState = get()) {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    var messageText by
    remember { mutableStateOf("") }
    var answer by
    remember { mutableStateOf<MessageModel?>(null) }
    val sender = DemoMemberModel
    val messageList = remember {
        mutableStateListOf(
            DemoMessageModel,
            DemoMessageModel,
            DemoMessageModelVeryLong,
            DemoMessageModel30Minutes,
            DemoMessageModelLongMessage,
            DemoMessageModel5Minutes,
            DemoHiddenImageMessage,
            DemoMessageModelLeaveChat,
            getDemoMessageModel(sender = DemoMemberModelTwo),
            getDemoMessageModel(sender = DemoMemberModelTwo),
            getDemoMessageModel(sender = DemoMemberModelTwo),
            DemoMessageModelScreenshot,
            DemoMyHiddenImageMessage,
            DemoImageMessage,
            DemoMessageModelJoinToChat,
            DemoMessageModel,
            DemoMessageModelCreated,
        )
    }
    //TODO - тут на 1 всегда больше т.к. при запуске съедает еденицу
    var unReadCount by remember { mutableStateOf(5) }
    val meet = DemoMeetingModel
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
    
    val listState = rememberLazyListState()
    
    LaunchedEffect(Unit) {
        scope.launch {
            listState.scrollToItem(unReadCount)
        }
    }
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    val meetBsCallback = object: MeetingBSCallback {
        override fun onKebabClick(state: Boolean) {
            menuState = state
        }
        
        override fun onAvatarClick() {
            scope.launch {
                asm.bottomSheetState.expand {
                    organizer(
                        DemoProfileModel, meet,
                        asm, scope
                    )
                }
            }
        }
        
        override fun onBottomButtonClick(point: Int) {
            scope.launch { asm.bottomSheetState.collapse() }
            meetOutAlert = true
        }
        
        override fun onMenuItemClick(index: Int) {
            scope.launch {
                asm.bottomSheetState.expand {
                    menuState = false
                    ComplainsContent(meet) {
                        scope.launch {
                            asm.bottomSheetState.collapse()
                        }; alert = true
                    }
                }
            }
        }
    }
    
    fun listToDown() {
        scope.launch {
            listState.animateScrollToItem(0)
        }
    }
    
    ChatContent(
        ChatState(
            ChatAppBarState(
                meet.title, DemoAvatarModel, (2)
            ), answer, meet, messageText,
            messageList, sender, alert,
            meetOutAlert, kebabMenuState,
            messageMenuState, listState, unReadCount
        ), Modifier, object: ChatCallback {
            override fun onBack() {
                nav.navigate("main")
            }
            
            override fun onImageClick(message: MessageModel) {
                nav.navigate(
                    "photo?image=${
                        message.attachments!!.file!!.id
                    }&type=2"
                )
            }
            
            override fun onDownButtonClick() {
                listToDown()
                unReadCount = 0
            }
            
            override fun onHiddenClick(message: MessageModel) {
                if(message.attachments?.file?.hasAccess == true)
                    Toast.makeText(
                        context,
                        "Скрытое фото больше недоступно к просмотру",
                        Toast.LENGTH_SHORT
                    ).show()
                else nav.navigate(
                    "photo?image=${
                        message.attachments!!.file!!.id
                    }&type=1"
                )
            }
            
            override fun onListDown() {
                unReadCount -= 1
            }
            
            override fun onSwipe(message: MessageModel) {
                answer = message
            }
            
            override fun onMessageMenuDismiss() {
                messageMenuState = false
            }
            
            override fun onMessageMenuItemSelect(point: Int, message: MessageModel) {
                when(point) {
                    0 -> answer = message
                    1 -> {
                        answer = null
                        messageList.remove(message)
                    }
                }
                messageMenuState = false
            }
            
            override fun textChange(text: String) {
                messageText = text
            }
            
            override fun gallery() {
                focusManager.clearFocus()
                scope.launch {
                    asm.bottomSheetState.expand {
                        HiddenPhotoBottomSheet()
                    }
                }
            }
            
            override fun onMeetOut() {
                Toast.makeText(
                    context,
                    "Вы покинули встречу ${meet.title}",
                    Toast.LENGTH_SHORT
                ).show()
                meetOutAlert = false
            }
            
            override fun onMeetOutAlertDismiss() {
                meetOutAlert = false
            }
            
            override fun onSend() {
                messageList.add(
                    0, MessageModel(
                        id = "1",
                        sender = DemoMemberModel,
                        album = "Бэтмен",
                        text = messageText,
                        attachments = null,
                        notification = null,
                        type = MESSAGE,
                        isRead = false,
                        isDelivered = true,
                        createdAt = "2022-10-17T08:35:54.140Z",
                        answer = answer
                    )
                )
                answer = null
                messageText = ""
                listToDown()
            }
            
            override fun onCancelAnswer() {
                answer = null
            }
            
            override fun closeAlert() {
                alert = false
            }
            
            override fun onAvatarClick() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        MeetingBottomSheet(
                            MeetingBSState(
                                menuState, meet,
                                DemoMemberModelList,
                                distanceCalculator(meet),
                                (true),
                            ), Modifier
                                .padding(16.dp)
                                .padding(bottom = 40.dp),
                            meetBsCallback
                        )
                    }
                }
            }
            
            override fun onMenuItemClick(point: Int) {
                when(point) {
                    0 -> meetOutAlert = true
                    1 -> scope.launch {
                        asm.bottomSheetState.expand {
                            menuState = false
                            ComplainsContent(meet) {
                                scope.launch {
                                    asm.bottomSheetState.collapse()
                                }; alert = true
                            }
                        }
                    }
                }
                kebabMenuState = !kebabMenuState
            }
            
            override fun onKebabClick() {
                kebabMenuState = !kebabMenuState
            }
        }
    )
}
