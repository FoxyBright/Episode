package ru.rikmasters.gilty.chat.presentation.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.*
import ru.rikmasters.gilty.chat.presentation.ui.dialog.*
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bars.ChatAppBarCallback
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bars.ChatAppBarState
import ru.rikmasters.gilty.chat.presentation.ui.dialog.bars.MessengerBarCallback
import ru.rikmasters.gilty.chat.presentation.ui.dialog.message.*
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainAlert
import ru.rikmasters.gilty.complaints.presentation.ui.MeetOutAlert
import ru.rikmasters.gilty.shared.R.string.*
import ru.rikmasters.gilty.shared.common.extentions.rememberDragRowState
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

data class DialogState(
    val topState: ChatAppBarState,
    val answer: MessageModel?,
    val meet: MeetingModel,
    val messageText: String,
    val messageList: List<MessageModel>,
    val user: UserModel,
    val alert: Boolean,
    val meetAlert: Boolean,
    val kebabMenuState: Boolean,
    val messageMenuState: Boolean,
    val imageMenuState: Boolean,
    val listState: LazyListState,
    val unReadCount: Int,
)

interface DialogCallback:
    ChatAppBarCallback,
    MessengerBarCallback,
    MessCallBack {
    
    fun closeAlert()
    fun onMenuItemClick(point: Int)
    fun onMeetOut()
    fun onMeetOutAlertDismiss()
    fun onMessageMenuDismiss()
    fun onImageMenuDismiss()
    fun onImageMenuItemSelect(point: Int)
    fun onDownButtonClick()
    fun onListDown()
    fun onSwipeToRefresh()
    fun onMessageMenuItemSelect(
        point: Int,
        message: MessageModel,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DialogContent(
    state: DialogState,
    modifier: Modifier = Modifier,
    callback: DialogCallback? = null,
) {
    Scaffold(
        modifier,
        topBar = {
            DialogTopBar(
                state.topState,
                state.kebabMenuState,
                Modifier, callback
            )
        },
        bottomBar = {
            DialogBottomBar(
                state.imageMenuState,
                state.messageText,
                state.answer,
                state.meet.isOnline,
                Modifier,
                callback
            )
        },
        floatingActionButton = {
            DialogFloatingButton(
                state.listState,
                state.unReadCount,
                state.meet.isOnline,
                Modifier, {
                    callback?.onListDown()
                }) {
                callback?.onDownButtonClick()
            }
        },
        content = {
            Content(
                it, state,
                Modifier,
                callback
            )
        }
    )
    
    ComplainAlert(
        state.alert,
        Modifier.padding(16.dp)
    ) { callback?.closeAlert() }
    
    MeetOutAlert(
        state.meetAlert,
        { callback?.onMeetOut() })
    { callback?.onMeetOutAlertDismiss() }
}

@Composable
private fun Content(
    padding: PaddingValues,
    state: DialogState,
    modifier: Modifier = Modifier,
    callback: DialogCallback?,
) {
    val list =
        state.messageList.map { mess ->
            mess to rememberDragRowState()
        }
    LazyColumn(
        modifier
            .padding(padding)
            .fillMaxSize()
            .background(colors.chatBack),
        state.listState,
        reverseLayout = true
    ) {
        item { Divider(Modifier, 28.dp, Transparent) }
        itemsIndexed(
            list, { _, mes -> mes.first.id })
        { index, (message, rowState) ->
            DialogMessage(
                message, rowState,
                (message.message?.is_author == true),
                state.meet.isOnline,
                if(index < list.size - 1)
                    list[index + 1].first
                else null, if(index in 1..list.size)
                    list[index - 1].first
                else null, callback
            )
        }
    }
}

@Composable
@Suppress("unused")
/*TODO отслеживает - когда лист пролистывают вверх
   (понадобится для регулирования плавающей кнопки)
*/
private fun LazyListState.isScrollingUp(
): Boolean {
    val firstIndex by remember {
        derivedStateOf { firstVisibleItemIndex }
    }
    val firstOffset by remember {
        derivedStateOf { firstVisibleItemScrollOffset }
    }
    var previousIndex by remember(this)
    { mutableStateOf(firstIndex) }
    var previousScrollOffset by remember(this)
    { mutableStateOf(firstOffset) }
    return remember(this) {
        derivedStateOf {
            if(previousIndex != firstIndex) {
                previousIndex > firstIndex
            } else {
                previousScrollOffset >= firstOffset
            }.also {
                previousIndex = firstIndex
                previousScrollOffset = firstOffset
            }
        }
    }.value
}