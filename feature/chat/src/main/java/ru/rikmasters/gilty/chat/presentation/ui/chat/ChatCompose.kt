package ru.rikmasters.gilty.chat.presentation.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.Message
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainAlert
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.meeting.*

data class ChatState(
    val topState: ChatAppBarState,
    val answer: MessageModel?,
    val meet: FullMeetingModel,
    val messageText: String,
    val messageList: List<MessageModel>,
    val sender: MemberModel,
    val alert: Boolean,
    val kebabMenuState: Boolean
)

interface ChatCallback:
    ChatAppBarCallback,
    MessengerBarCallback {
    
    fun closeAlert() {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatContent(
    state: ChatState,
    modifier: Modifier = Modifier,
    callback: ChatCallback? = null
) {
    ComplainAlert(
        state.alert,
        Modifier.padding(16.dp)
    ) { callback?.closeAlert() }
    Scaffold(
        modifier,
        {
            ChatAppBarContent(
                state.topState, Modifier,
                callback
            )
        },
        {
            MessengerBar(
                state.messageText, Modifier,
                state.answer, callback
            )
        }
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            LazyColumn(Modifier.align(Alignment.BottomCenter)) {
                items(state.messageList) { mes ->
                    Message(
                        mes, (state.sender == mes.sender), state.answer
                    )
                }
                item { Divider(Modifier, 10.dp, Color.Transparent) }
            }
        }
    }
}