package ru.rikmasters.gilty.chat.presentation.ui.chat

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.ui.chat.bottom.HiddenPhotoBottomSheet
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen.MeetingClick
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModelList
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel

@Composable
fun ChatScreen(nav: NavState = get()) {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    var messageText by
    remember { mutableStateOf("") }
    var answer by
    remember { mutableStateOf<MessageModel?>(null) }
    val sender = DemoMemberModel
    val messageList =
        remember {
            mutableStateListOf(
                DemoImageMessage,
                DemoMessageModelLongMessage,
                DemoMessageModel
            )
        }
    
    val meet =
        DemoFullMeetingModel
    var alert by
    remember { mutableStateOf(false) }
    var menuState by
    remember { mutableStateOf(false) }
    var kebabMenuState by
    remember { mutableStateOf(false) }
    
    ChatContent(
        ChatState(
            ChatAppBarState(
                meet.title, DemoAvatarModel, 2
            ), answer, meet, messageText,
            messageList, sender, alert, kebabMenuState
        ), Modifier, object: ChatCallback {
            override fun onBack() {
                nav.navigate("main")
            }
            
            override fun textChange(text: String) {
                messageText = text
            }
            
            override fun gallery() {
                scope.launch {
                    asm.bottomSheetState.expand {
                        HiddenPhotoBottomSheet()
                    }
                }
            }
            
            override fun onSend() {
                messageList.add(
                    MessageModel(
                        id = "1",
                        sender = DemoMemberModel,
                        album = "Бэтмен",
                        text = messageText,
                        attachments = answer?.attachments,
                        isRead = false,
                        isDelivered = true,
                        createdAt = "2022-10-17T08:35:54.140Z",
                    )
                )
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
                        MeetingClick(
                            menuState, { menuState = it },
                            {
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
                            }, meet, DemoMemberModelList,
                            {
                                nav.navigateAbsolute(
                                    "main/reaction?avatar=${meet.organizer.avatar.id}"
                                ); scope.launch { asm.bottomSheetState.collapse() }
                            }
                        )
                    }
                }
            }
            
            override fun onKebabClick() {
                kebabMenuState = true
            }
        }
    )
}