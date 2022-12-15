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
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.Message
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainAlert
import ru.rikmasters.gilty.complaints.presentation.ui.MeetOutAlert
import ru.rikmasters.gilty.shared.R.string.exit_from_meet
import ru.rikmasters.gilty.shared.R.string.meeting_complain
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.GDropMenu

data class ChatState(
    val topState: ChatAppBarState,
    val answer: MessageModel?,
    val meet: FullMeetingModel,
    val messageText: String,
    val messageList: List<MessageModel>,
    val sender: MemberModel,
    val alert: Boolean,
    val meetAlert: Boolean,
    val kebabMenuState: Boolean
)

interface ChatCallback:
    ChatAppBarCallback,
    MessengerBarCallback {
    
    fun closeAlert() {}
    fun onMenuItemClick(point: Int) {}
    fun onMeetOut() {}
    fun onMeetOutAlertDismiss() {}
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
    MeetOutAlert(
        state.meetAlert,
        { callback?.onMeetOut() })
    { callback?.onMeetOutAlertDismiss() }
    Box(modifier) {
        GDropMenu(
            state.kebabMenuState,
            { callback?.onKebabClick() },
            menuItem = listOf(
                Pair(stringResource(exit_from_meet))
                { callback?.onMenuItemClick(0) },
                Pair(stringResource(meeting_complain))
                { callback?.onMenuItemClick(1) }
            )
        )
    }
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
            LazyColumn(Modifier.align(BottomCenter)) {
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