package ru.rikmasters.gilty.chat.presentation.ui.chat

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.ChatAppBarContent
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.ChatAppBarState
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.MessengerBar
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.MessState
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.Message
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.messageShapes
import ru.rikmasters.gilty.chat.presentation.ui.chat.popUp.BottomBarMenu
import ru.rikmasters.gilty.chat.presentation.ui.chat.popUp.MessageMenu
import ru.rikmasters.gilty.chat.presentation.ui.chat.popUp.TopBarMenu
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.vibrate
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.MESSAGE
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.NOTIFICATION

@Composable
fun ChatTopBar(
    state: ChatAppBarState,
    menuState: Boolean,
    modifier: Modifier = Modifier,
    callback: ChatCallback?,
) {
    ChatAppBarContent(state, modifier, callback)
    TopBarMenu(menuState,
        { callback?.onKebabClick() })
    { callback?.onMenuItemClick(it) }
}

@Composable
fun ChatBottomBar(
    menuState: Boolean,
    text: String,
    answer: MessageModel?,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    callback: ChatCallback?,
) {
    BottomBarMenu(menuState,
        { callback?.onImageMenuDismiss() })
    { callback?.onImageMenuItemSelect(it) }
    MessengerBar(text, modifier, answer, isOnline, callback)
}

@Composable
fun ChatFloatingButton(
    listState: LazyListState,
    unReadCount: Int,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    onListDown: () -> Unit,
    onClick: () -> Unit,
) {
    val first = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }.value
    val unRead = "${
        if(unReadCount <= first) unReadCount
        else {
            onListDown()
            unReadCount
        }
    }"
    val offset = when(unRead.length) {
        0 -> DpOffset(0.dp, 0.dp)
        1 -> DpOffset(4.dp, 4.dp)
        2 -> DpOffset(6.dp, 4.dp)
        else -> DpOffset(8.dp, 4.dp)
    }
    Box(
        modifier
            .offset(
                animateDpAsState(
                    if(first < 1)
                        100.dp else 0.dp,
                    tween(400)
                ).value
            )
            .padding(
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
                painterResource(
                    R.drawable.ic_down_arrow
                ),
                (null), Modifier,
            )
        }
        if(unReadCount > 0) Box(
            Modifier
                .offset(offset.x, -offset.y)
                .background(
                    if(isOnline) colorScheme.secondary
                    else colorScheme.primary,
                    CircleShape
                )
                .align(TopEnd)
        ) {
            Text(
                unRead, Modifier.padding(
                    horizontal = if(
                        unRead.length == 1
                    ) 8.dp else 4.dp,
                    vertical = 2.dp
                ), Color.White,
                style = typography.headlineSmall
            )
        }
    }
}

@Composable
fun ChatMessage(
    message: MessageModel,
    state: DragRowState,
    sender: Boolean,
    isOnline: Boolean,
    last: MessageModel? = null,
    next: MessageModel? = null,
    callback: ChatCallback? = null,
) {
    val context = LocalContext.current
    val type = message.type
    var offset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    var menuState by
    remember { mutableStateOf(false) }
    Message(
        MessState(
            message, sender, state,
            messageShapes(
                type, sender, last, next,
            ), (message.message?.author != next?.message?.author),
            isOnline
        ), Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        if(type == MESSAGE) {
                            vibrate(context)
                            menuState = true
                            offset = it
                        }
                    }
                )
            }
            .padding(
                16.dp, if(type != NOTIFICATION)
                    2.dp else 10.dp
            ),
        callback
    )
    MessageMenu(menuState, offset, { menuState = false })
    { callback?.onMessageMenuItemSelect(it, message) }
}