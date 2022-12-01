package ru.rikmasters.gilty.chat.presentation.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.model.DemoImageMessage
import ru.rikmasters.gilty.chat.presentation.model.DemoMessageModel
import ru.rikmasters.gilty.chat.presentation.model.DemoMessageModelLongMessage
import ru.rikmasters.gilty.chat.presentation.model.MessageModel
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.Message
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {

    // TODO экран тестовый, для просмотра поведения сообщений

    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val chatAppBarState = ChatAppBarState(
        "Бэтмен", DemoAvatarModel, 2
    )
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

    Scaffold(
        Modifier, { ChatAppBarContent(chatAppBarState) },
        {
            MessengerBar(messageText, Modifier, answer, object : MessengerBarCallback {
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

                override fun textChange(text: String) {
                    messageText = text
                }

                override fun onCancelAnswer() {
                    answer = null
                }

                override fun gallery() {
                    scope.launch {
                        asm.bottomSheetState.expand {
                            Text(
                                "Скрытые фотки",
                                Modifier.height(300.dp)
                            )
                        }
                    }
                }
            })
        }
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            LazyColumn(Modifier.align(Alignment.BottomCenter)) {
                items(messageList) { mes ->
                    Message(mes, sender == mes.sender, answer)
                }
                item { Divider(Modifier, 10.dp, Color.Transparent) }
            }
        }
    }
}