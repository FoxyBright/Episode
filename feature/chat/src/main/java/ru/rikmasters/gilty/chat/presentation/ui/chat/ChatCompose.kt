package ru.rikmasters.gilty.chat.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.Message
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainAlert
import ru.rikmasters.gilty.complaints.presentation.ui.MeetOutAlert
import ru.rikmasters.gilty.shared.R.string.*
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.GDropMenu

data class ChatState(
    val topState: ChatAppBarState,
    val answer: MessageModel?,
    val meet: MeetingModel,
    val messageText: String,
    val messageList: List<MessageModel>,
    val sender: MemberModel,
    val alert: Boolean,
    val meetAlert: Boolean,
    val kebabMenuState: Boolean,
    val messageMenuState: Boolean,
    val listState: LazyListState
)

interface ChatCallback:
    ChatAppBarCallback,
    MessengerBarCallback {
    
    fun closeAlert() {}
    fun onMenuItemClick(point: Int) {}
    fun onMeetOut() {}
    fun onMeetOutAlertDismiss() {}
    fun onMessageLongClick(message: MessageModel) {}
    fun onMessageMenuItemSelect(point: Int) {}
    fun onMessageMenuDismiss() {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatContent(
    state: ChatState,
    modifier: Modifier = Modifier,
    callback: ChatCallback? = null,
) {
    ComplainAlert(
        state.alert,
        Modifier.padding(16.dp)
    ) { callback?.closeAlert() }
    MeetOutAlert(
        state.meetAlert,
        { callback?.onMeetOut() })
    { callback?.onMeetOutAlertDismiss() }
    Box(Modifier) {
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
        modifier, {
            ChatAppBarContent(
                state.topState, Modifier,
                callback
            )
        }, {
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
            LazyColumn(Modifier.align(BottomCenter), state.listState) {
                items(state.messageList) { mes ->
                    Message(
                        mes, (state.sender == mes.sender), mes.answer
                    ) { message -> callback?.onMessageLongClick(message) }
                }
                item { Divider(Modifier, 10.dp, Color.Transparent) }
                item {
                    MessageMenu(state.messageMenuState, {
                        callback?.onMessageMenuDismiss()
                    }, Modifier.fillMaxWidth())
                    { point -> callback?.onMessageMenuItemSelect(point) }
                }
            }
        }
    }
}

@Composable
private fun MessageMenu(
    state: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Box(modifier) {
        DropdownMenu(
            state, onDismiss,
            Modifier.background(
                colorScheme.primaryContainer
            )
        ) {
            DropdownMenuItem({
                Text(
                    stringResource(answer_button),
                    color = colorScheme.tertiary,
                    style = typography.bodyMedium
                )
            }, { onClick(0) })
            DropdownMenuItem({
                Text(
                    stringResource(meeting_filter_delete_tag_label),
                    color = colorScheme.primary,
                    style = typography.bodyMedium
                )
            }, { onClick(1) })
        }
    }
}