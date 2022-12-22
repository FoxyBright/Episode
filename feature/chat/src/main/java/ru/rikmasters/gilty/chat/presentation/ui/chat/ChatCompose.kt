package ru.rikmasters.gilty.chat.presentation.ui.chat

import android.content.res.Resources.getSystem
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.chat.presentation.ui.chat.bottom.GPopUpMenu
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.*
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainAlert
import ru.rikmasters.gilty.complaints.presentation.ui.MeetOutAlert
import ru.rikmasters.gilty.shared.R.drawable.ic_down_arrow
import ru.rikmasters.gilty.shared.R.string.*
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.rememberDragRowState
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.chat.MessageType.NOTIFICATION
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.shared.GDropMenu

data class ChatState(
    val topState: ChatAppBarState,
    val answer: MessageModel?,
    val meet: MeetingModel,
    val messageText: String,
    val messageList: List<MessageModel>,
    val user: MemberModel,
    val alert: Boolean,
    val meetAlert: Boolean,
    val kebabMenuState: Boolean,
    val messageMenuState: Boolean,
    val listState: LazyListState,
    val unReadCount: Int
)

interface ChatCallback:
    ChatAppBarCallback,
    MessengerBarCallback,
    MessCallBack {
    
    fun closeAlert()
    fun onMenuItemClick(point: Int)
    fun onMeetOut()
    fun onMeetOutAlertDismiss()
    fun onMessageMenuDismiss()
    fun onDownButtonClick()
    fun onListDown()
    fun onMessageMenuItemSelect(
        point: Int,
        message: MessageModel
    )
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
                state.topState,
                Modifier
                    .navigationBarsPadding()
                    .imePadding(), callback
            )
        }, {
            MessengerBar(
                state.messageText, Modifier,
                state.answer, callback
            )
        },
        floatingActionButton = {
            val first = state.listState
                .firstVisibleItemIndex
            val unread = state.unReadCount
            DownButton(
                if(unread <= first) unread
                else {
                    callback?.onListDown()
                    unread
                }, modifier.offset(
                    animateDpAsState(
                        if(first < 1)
                            100.dp else 0.dp,
                        tween(400)
                    ).value
                )
            ) { callback?.onDownButtonClick() }
        }
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            val list =
                state.messageList.map { mess ->
                    mess to rememberDragRowState()
                }
            LazyColumn(
                Modifier.align(BottomCenter),
                state.listState,
                reverseLayout = true
            ) {
                itemsIndexed(
                    list,
                    { index, _ -> index }) // TODO проверку на сообщения (должны быть уникальны)
                { index, mes ->
                    Item(
                        mes.first, mes.second,
                        (state.user == mes.first.sender),
                        if(index < list.size - 1)
                            list[index + 1].first
                        else null, if(index in 1..list.size)
                            list[index - 1].first
                        else null, callback
                    )
                }
            }
        }
    }
}

@Composable
private fun Item(
    message: MessageModel,
    state: DragRowState,
    sender: Boolean,
    last: MessageModel? = null,
    next: MessageModel? = null,
    callback: ChatCallback?
) {
    val type = message.type
    val messType = type != NOTIFICATION
    val density = getSystem()
        .displayMetrics.density
    var offset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    var menuState by
    remember { mutableStateOf(false) }
    Box(Modifier.pointerInput(Unit) {
        detectTapGestures(
            onLongPress = { off ->
                if(messType) {
                    menuState = true
                    offset = off
                }
            }
        )
    }) {
        Message(
            MessState(
                message, sender, state,
                MessageShapes(
                    type, sender,
                    last, next
                )
            ), Modifier
                .padding(
                    16.dp, if(messType)
                        2.dp else 10.dp
                ), callback
        )
        val dismiss = { menuState = false }
        GPopUpMenu(
            menuState, dismiss, listOf(
                Triple(
                    stringResource(answer_button),
                    colorScheme.tertiary
                ) {
                    dismiss()
                    callback?.onMessageMenuItemSelect(
                        0, message
                    )
                }, Triple(
                    stringResource(meeting_filter_delete_tag_label),
                    colorScheme.primary
                ) {
                    dismiss()
                    callback?.onMessageMenuItemSelect(
                        1, message
                    )
                }
            ), Modifier.offset(
                (offset.x / density).dp,
                (offset.y / density).dp
            )
        )
    }
}

@Composable
private fun DownButton(
    unReadCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val unRead = "$unReadCount"
    val offset = when(unRead.length) {
        0 -> DpOffset(0.dp, 0.dp)
        1 -> DpOffset(4.dp, 4.dp)
        2 -> DpOffset(6.dp, 4.dp)
        else -> DpOffset(8.dp, 4.dp)
    }
    Card(
        modifier, elevation = CardDefaults.cardElevation(0.dp),
        colors = cardColors(Transparent)
    ) {
        Box(
            Modifier.padding(
                end = offset.x,
                top = offset.y
            )
        ) {
            IconButton(
                onClick, Modifier
                    .background(
                        colorScheme.primaryContainer,
                        CircleShape
                    )
            ) {
                Icon(
                    painterResource(ic_down_arrow),
                    (null), Modifier,
                )
            }
            if(unReadCount > 0) Box(
                Modifier
                    .offset(offset.x, -offset.y)
                    .background(
                        colorScheme.primary,
                        CircleShape
                    )
                    .align(TopEnd)
            ) {
                Text(
                    unRead, Modifier.padding(
                        horizontal = if(unRead.length == 1)
                            8.dp else 4.dp,
                        vertical = 2.dp
                    ),
                    White, style = typography.headlineSmall
                )
            }
        }
    }
}

@Composable
@Suppress("unused") //TODO отслеживает - когда лист пролистывают вверх
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this)
    { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this)
    { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if(previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}