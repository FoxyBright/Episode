package ru.rikmasters.gilty.chat.presentation.ui.chatlist

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
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.string.chats_ended_chats_label
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.DemoChatListModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
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
                    NavIconState.INACTIVE,
                    NavIconState.NEW,
                    NavIconState.INACTIVE,
                    NavIconState.INACTIVE,
                    NavIconState.ACTIVE
                ), getSortedChats(DemoChatListModel), (true)
            ), Modifier.background(colorScheme.background)
        )
    }
}

data class ChatListState(
    val stateList: List<NavIconState>,
    val chats: Pair<
            List<Pair<String, List<ChatModel>>>,
            List<ChatModel>>,
    val endedState: Boolean
)

interface ChatListCallback {
    
    fun onNavBarSelect(point: Int) {}
    fun onChatClick(chat: ChatModel) {}
    fun onEndedClick() {}
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
                        label(
                            it.first, Modifier.padding(
                                top = 28.dp,
                                bottom = 18.dp
                            )
                        )
                        list(it.second) {
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
                    if(state.endedState)
                        list(state.chats.second)
                        { callback?.onChatClick(it) }
                }
            }
        }
    }
    Box(Modifier.fillMaxSize()) {
        NavBar(
            state.stateList, Modifier
                .align(Alignment.BottomCenter)
        ) { callback?.onNavBarSelect(it) }
    }
}

@Composable
private fun list(
    list: List<ChatModel>,
    onClick: (ChatModel) -> Unit
) {
    list.forEachIndexed { index, it ->
        ChatRowContent(
            Modifier, it, LazyItemsShapes(
                index, list.size
            )
        ) { onClick(it) }
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
        label(stringResource(chats_ended_chats_label))
        Icon(
            if(state) Filled.KeyboardArrowDown
            else Filled.KeyboardArrowRight,
            (null), Modifier.size(24.dp)
        )
    }
}

@Composable
private fun label(
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
            R.drawable.empty_chat_zaglushka
        ), (null), modifier.fillMaxSize()
    )
}