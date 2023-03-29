package ru.rikmasters.gilty.chat.presentation.ui.chatList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.*
import kotlinx.coroutines.flow.flowOf
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.ChatDeleteAlert
import ru.rikmasters.gilty.chat.viewmodel.ChatListViewModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.empty_chat_zaglushka
import ru.rikmasters.gilty.shared.R.string.chats_ended_chats_label
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.rememberDragRowState
import ru.rikmasters.gilty.shared.model.chat.ChatModel
import ru.rikmasters.gilty.shared.model.chat.DemoChatModelList
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
private fun previewData() = flowOf(
    PagingData.from(DemoChatModelList)
).collectAsLazyPagingItems()

@Preview
@Composable
private fun ChatListPreview() {
    GiltyTheme {
        ChatListContent(
            ChatListState(
                listOf(
                    INACTIVE, INACTIVE, INACTIVE,
                    INACTIVE, ACTIVE
                ), previewData(),
                (true), (false), LIST, (1),
                (false), LazyListState(), listOf()
            ),
            Modifier.background(colorScheme.background),
        )
    }
}

data class ChatListState(
    val stateList: List<NavIconState>,
    val chats: LazyPagingItems<ChatModel>,
    val endedState: Boolean,
    val alertActive: Boolean,
    val alertState: AlertState,
    val alertSelect: Int,
    val unRead: Boolean,
    val listState: LazyListState,
    val unreadChats: List<ChatModel>,
)

interface ChatListCallback {
    
    fun onNavBarSelect(point: Int)
    fun onChatClick(chat: ChatModel)
    fun onChatSwipe(chat: ChatModel)
    fun onEndedClick()
    fun onAlertSuccess()
    fun onAlertDismiss()
    fun onUnreadChange()
    fun onListUpdate()
    fun onListAlertSelect(index: Int)
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatListContent(
    state: ChatListState,
    modifier: Modifier = Modifier,
    callback: ChatListCallback? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(Modifier, state.unRead)
            { callback?.onUnreadChange() }
        },
        bottomBar = {
            NavBar(state.stateList, Modifier)
            { callback?.onNavBarSelect(it) }
        },
        content = {
            Box(Modifier.padding(it)) {
                Use<ChatListViewModel>(PullToRefreshTrait) {
                    Content(
                        state,
                        Modifier,
                        callback
                    )
                }
            }
        }
    )
    ChatDeleteAlert(
        state.alertActive,
        state.alertState,
        state.alertSelect,
        { callback?.onListAlertSelect(it) },
        { callback?.onAlertDismiss() },
        { callback?.onAlertSuccess() })
}

@Composable
private fun TopBar(
    modifier: Modifier,
    unRead: Boolean,
    onUnReadChange: () -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 80.dp, bottom = 10.dp)
            .padding(horizontal = 16.dp),
        SpaceBetween, CenterVertically
    ) {
        Text(
            stringResource(R.string.chats_label),
            Modifier, colorScheme.tertiary,
            style = typography.titleLarge
        )
        Image(
            painterResource(R.drawable.ic_chat_indicator),
            (null), Modifier
                .size(32.dp)
                .clickable(
                    MutableInteractionSource(), (null)
                ) { onUnReadChange() },
            colorFilter = ColorFilter.tint(
                if(unRead) colorScheme.primary
                else colorScheme.onTertiary
            )
        )
    }
}

@Composable
private fun Content(
    state: ChatListState,
    modifier: Modifier = Modifier,
    callback: ChatListCallback?,
) {
    val chats = state.chats
    val itemCount = chats.itemCount
    
    if(LocalInspectionMode.current) PreviewLazy()
    else LazyColumn(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        state.listState
    ) {
        
        if(itemCount != 0) {
            
            val indices = arrayListOf<Int>()
            var i = 0
            
            try {
                while(i in 0 until itemCount) {
                    val chat = chats[i]!!
                    if(chat.meetStatus == MeetStatusType.ACTIVE)
                        indices.add(i)
                    ++i
                }
            } catch(e: Exception) {
                e.stackTraceToString()
            }
            
            getSortedChats(indices.map { chats[it]!! }).let {
                return@let if(it.isNotEmpty() && !state.unRead) it
                else null
            }?.let { pairs ->
                itemsIndexed(pairs) { index, (label, list) ->
                    Label(label, Modifier.padding(top = 28.dp, bottom = 18.dp))
                    list.map {
                        it to rememberDragRowState()
                    }.forEach { (chat, row) ->
                        ElementChat(
                            chat, index, list.size,
                            row, callback
                        )
                    }
                }
            }
            
            if(!state.unRead) item {
                ActionRow(
                    Modifier.padding(
                        top = 28.dp,
                        bottom = 18.dp,
                        end = 16.dp
                    ), state.endedState
                ) { callback?.onEndedClick() }
            }
            
            itemsIndexed(chats) { index, item ->
                if(item?.meetStatus != MeetStatusType.ACTIVE) {
                    val chat = item!! to rememberDragRowState()
                    ElementChat(
                        chat.first, index, itemCount,
                        chat.second, callback
                    )
                }
            }
            
            (itemCount).let {
                if(it > 0 && !state.unRead) items(it) { item ->
                    (chats[item]!! to rememberDragRowState())
                        .let { (chat, row) ->
                            ElementChat(
                                chat, item, itemCount,
                                row, callback
                            )
                        }
                }
            }
            
            item { PagingLoader(chats.loadState) }
        } else item { EmptyChats() }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
private fun PreviewLazy() {
    val list = DemoChatModelList.map {
        it to rememberDragRowState()
    }
    LazyColumn(Modifier.padding(horizontal = 16.dp)) {
        item { Label(stringResource(R.string.chats_unread)) }
        itemsIndexed(list) { i, (chat, row) ->
            ElementChat(chat, i, list.size, row)
        }
    }
}

@Composable
private fun ElementChat(
    chat: ChatModel,
    index: Int, size: Int,
    rowState: DragRowState,
    callback: ChatListCallback? = null,
) {
    val shape = lazyItemsShapes(index, size)
    Column(
        Modifier.background(
            colorScheme.primaryContainer,
            shape
        )
    ) {
        SwipeableChatRow(
            rowState, chat, shape, Modifier,
            { callback?.onChatClick(it) })
        { callback?.onChatSwipe(it) }
        if(index < size - 2) Divider(
            Modifier.padding(start = 78.dp)
        )
    }
}

@Composable
private fun ActionRow(
    modifier: Modifier = Modifier,
    state: Boolean = false,
    onClick: () -> Unit,
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
    modifier: Modifier = Modifier,
) = Text(
    text, modifier, colorScheme.tertiary,
    style = typography.labelLarge
)

@Composable
private fun EmptyChats(
    //TODO Сделать скрин с плавающим баблом
    modifier: Modifier = Modifier,
) {
    Image(
        painterResource(
            empty_chat_zaglushka
        ), (null), modifier.fillMaxSize()
    )
}