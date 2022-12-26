package ru.rikmasters.gilty.chat.presentation.ui.chatList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.ChatDeleteAlert
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.empty_chat_zaglushka
import ru.rikmasters.gilty.shared.R.string.chats_ended_chats_label
import ru.rikmasters.gilty.shared.common.extentions.rememberDragRowState
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.DemoChatListModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.shared.Divider
import ru.rikmasters.gilty.shared.shared.LazyItemsShapes
import ru.rikmasters.gilty.shared.shared.NavBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ChatListPreview() {
    GiltyTheme {
        ChatListContent(
            ChatListState(
                listOf(
                    INACTIVE, NEW, INACTIVE,
                    INACTIVE, ACTIVE
                ), getSortedChats(DemoChatListModel),
                (true), (false), LIST, listOf()
            ), Modifier.background(colorScheme.background)
        )
    }
}

data class ChatListState(
    val stateList: List<NavIconState>,
    val chats: Pair<
            List<Pair<String, List<ChatModel>>>,
            List<ChatModel>>,
    val endedState: Boolean,
    val alertActive: Boolean,
    val alertState: AlertState,
    val alertList: List<Pair<String, Boolean>>
)

interface ChatListCallback {
    
    fun onNavBarSelect(point: Int) {}
    fun onChatClick(chat: ChatModel) {}
    fun onChatSwipe(chat: ChatModel) {}
    fun onEndedClick() {}
    fun onAlertSuccess() {}
    fun onAlertDismiss() {}
    fun listAlertSelect(index: Int) {}
}

@Composable
fun ChatListContent(
    state: ChatListState,
    modifier: Modifier = Modifier,
    callback: ChatListCallback? = null
) {
    Column(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {
        Text(
            stringResource(R.string.chats_label),
            Modifier
                .padding(top = 80.dp, bottom = 10.dp)
                .padding(horizontal = 16.dp),
            colorScheme.tertiary,
            style = typography.titleLarge
        )
        if(state.chats.first.isEmpty()
            && state.chats.second.isEmpty()
        ) EmptyChats()
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if(state.chats.first.isNotEmpty())
                items(state.chats.first) {
                    Column {
                        Label(
                            it.first, Modifier.padding(
                                top = 28.dp,
                                bottom = 18.dp
                            )
                        )
                        List(it.second,
                            { callback?.onChatSwipe(it) }) {
                            callback?.onChatClick(it)
                        }
                    }
                }
            if(state.chats.second.isNotEmpty()) item {
                Column(modifier) {
                    ActionRow(
                        Modifier.padding(
                            top = 28.dp,
                            bottom = 18.dp,
                            end = 16.dp
                        ), state.endedState
                    ) { callback?.onEndedClick() }
                    if(state.endedState) List(state.chats.second,
                        { callback?.onChatSwipe(it) })
                    { callback?.onChatClick(it) }
                }
            }
        }
    }
    Box(Modifier.fillMaxSize()) {
        NavBar(
            state.stateList, Modifier
                .align(BottomCenter)
        ) { callback?.onNavBarSelect(it) }
    }
    ChatDeleteAlert(
        state.alertActive,
        state.alertState, state.alertList,
        { callback?.listAlertSelect(it) },
        { callback?.onAlertDismiss() },
        { callback?.onAlertSuccess() })
}

@Composable
private fun List(
    list: List<ChatModel>,
    onClick: (ChatModel) -> Unit,
    onSwipe: (ChatModel) -> Unit
) {
    Column(
        Modifier.background(
            colorScheme.primaryContainer,
            shapes.medium
        )
    ) {
        list.map {
            it to rememberDragRowState()
        }.forEachIndexed { index, it ->
            SwipeableChatRow(
                it.second, it.first,
                LazyItemsShapes(
                    index, list.size
                ), index, //TODO <- Сюда кол-во непрочитанных
                Modifier, { onSwipe(it) }
            ) { onClick(it) }
            if(index < list.size - 1)
                Divider(Modifier.padding(start = 78.dp))
        }
    }
}

@Composable
private fun ActionRow(
    modifier: Modifier = Modifier,
    state: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier.clickable(
            MutableInteractionSource(), (null)
        ) { onClick() }, Start, CenterVertically
    ) {
        Label(stringResource(chats_ended_chats_label))
        Icon(
            if(state) Filled.KeyboardArrowDown
            else Filled.KeyboardArrowRight,
            (null), Modifier.size(24.dp),
            colorScheme.tertiary
        )
    }
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier = Modifier
) = Text(
    text, modifier, colorScheme.tertiary,
    style = typography.labelLarge
)

@Composable
private fun EmptyChats(  //TODO Перерисовать нормальным образом без спешки
    modifier: Modifier = Modifier
) {
    Image(
        painterResource(
            empty_chat_zaglushka
        ), (null), modifier.fillMaxSize()
    )
}