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
import androidx.paging.LoadState
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
import ru.rikmasters.gilty.shared.model.chat.SortTypeModel
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
                    INACTIVE,
                    INACTIVE,
                    INACTIVE,
                    INACTIVE,
                    ACTIVE
                ),
                previewData(),
                (true),
                (false),
                LIST,
                (1),
                (SortTypeModel.MEETING_DATE),
                LazyListState()
            ),
            Modifier.background(colorScheme.background)
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
    val sortType: SortTypeModel,
    val listState: LazyListState
)

interface ChatListCallback {

    fun onNavBarSelect(point: Int)
    fun onChatClick(chat: ChatModel)
    fun onChatSwipe(chat: ChatModel)
    fun onEndedClick()
    fun onAlertSuccess()
    fun onAlertDismiss()
    fun onSortTypeChanged(sortType: SortTypeModel)
    fun onListUpdate()
    fun onListAlertSelect(index: Int)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatListContent(
    state: ChatListState,
    modifier: Modifier = Modifier,
    callback: ChatListCallback? = null
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(Modifier, state.sortType) { callback?.onSortTypeChanged(it) }
        },
        bottomBar = {
            NavBar(state.stateList, Modifier) { callback?.onNavBarSelect(it) }
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
        { callback?.onAlertSuccess() }
    )
}

@Composable
private fun TopBar(
    modifier: Modifier,
    sortType: SortTypeModel,
    onSortTypeChanged: (SortTypeModel) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 80.dp, bottom = 10.dp)
            .padding(horizontal = 16.dp),
        SpaceBetween,
        CenterVertically
    ) {
        Text(
            stringResource(R.string.chats_label),
            Modifier,
            colorScheme.tertiary,
            style = typography.titleLarge
        )
        Image(
            painterResource(R.drawable.ic_chat_indicator),
            (null),
            Modifier
                .size(32.dp)
                .clickable(
                    MutableInteractionSource(),
                    (null)
                ) {
                    onSortTypeChanged(
                        if (sortType == SortTypeModel.MEETING_DATE) SortTypeModel.MESSAGE_DATE
                        else SortTypeModel.MEETING_DATE
                    )
                },
            colorFilter = ColorFilter.tint(
                if (sortType == SortTypeModel.MESSAGE_DATE) colorScheme.primary
                else colorScheme.onTertiary
            )
        )
    }
}

@Composable
private fun Content(
    state: ChatListState,
    modifier: Modifier = Modifier,
    callback: ChatListCallback?
) {
    val chats = state.chats
    val itemCount = chats.itemCount

    if (LocalInspectionMode.current) PreviewLazy()
    else LazyColumn(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        state.listState
    ) {
        when {
            chats.loadState.refresh is LoadState.Error -> {}
            chats.loadState.append is LoadState.Error -> {}
            else -> {
                if (chats.loadState.refresh is LoadState.Loading) {
                    item { PagingLoader(state.chats.loadState) }
                }
                if (itemCount != 0) {
                    getSortedChats(
                        state.chats.itemSnapshotList.items.filter {
                            it.meetStatus == MeetStatusType.ACTIVE
                        }
                    ).let {
                        if (it.isNotEmpty() && state.sortType == SortTypeModel.MEETING_DATE) {
                            items(it) { (label, list) ->
                                Label(label, Modifier.padding(top = 28.dp, bottom = 18.dp))
                                list.map { chatModel ->
                                    chatModel to rememberDragRowState()
                                }.forEachIndexed { index, (chat, row) ->
                                    ElementChat(
                                        chat,
                                        index,
                                        list.size,
                                        row,
                                        callback
                                    )
                                }
                            }
                        }
                    }
                    val inactiveItems = state.chats.itemSnapshotList.items.filter {
                        it.meetStatus != MeetStatusType.ACTIVE
                    }
                    if (state.sortType == SortTypeModel.MEETING_DATE && inactiveItems.isNotEmpty()) item {
                        ActionRow(
                            Modifier.padding(
                                top = 28.dp,
                                bottom = 18.dp,
                                end = 16.dp
                            ),
                            state.endedState
                        ) { callback?.onEndedClick() }
                    }

                    if (state.endedState || state.sortType == SortTypeModel.MESSAGE_DATE) {
                        itemsIndexed(state.chats) { index, item ->
                            if (item?.meetStatus != MeetStatusType.ACTIVE) {
                                val chat = (item ?: ChatModel()) to rememberDragRowState()
                                val inactiveChatsSize = state.chats.itemSnapshotList.items.filter {
                                    it.meetStatus != MeetStatusType.ACTIVE
                                }.size
                                val indexCalc = if (index == (itemCount - inactiveChatsSize)) {
                                    0
                                } else index
                                ElementChat(
                                    chat.first,
                                    indexCalc,
                                    itemCount,
                                    chat.second,
                                    callback
                                )
                            }
                        }
                    }
                    if (state.chats.loadState.append is LoadState.Loading && state.endedState) {
                        item { PagingLoader(state.chats.loadState) }
                    }
                } else {
                    if (chats.loadState.refresh is LoadState.NotLoading) {
                        item { EmptyChats() }
                    }
                }
            }
        }
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
    index: Int,
    size: Int,
    rowState: DragRowState,
    callback: ChatListCallback? = null
) {
    val shape = lazyItemsShapes(index, size)
    Column(
        Modifier.background(
            colorScheme.primaryContainer,
            shape
        )
    ) {
        SwipeableChatRow(
            rowState,
            chat,
            shape,
            Modifier,
            { callback?.onChatClick(it) }
        ) { callback?.onChatSwipe(it) }
        if (index < size - 1) Divider(
            Modifier.padding(start = 78.dp)
        )
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
            MutableInteractionSource(),
            (null)
        ) { onClick() },
        Start,
        CenterVertically
    ) {
        Label(stringResource(chats_ended_chats_label))
        Icon(
            if (state) Filled.KeyboardArrowDown
            else Filled.KeyboardArrowRight,
            (null),
            Modifier.size(24.dp),
            colorScheme.tertiary
        )
    }
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier = Modifier
) = Text(
    text,
    modifier,
    colorScheme.tertiary,
    style = typography.labelLarge
)

@Composable
private fun EmptyChats(
    // TODO Сделать скрин с плавающим баблом
    modifier: Modifier = Modifier
) {
    Image(
        painterResource(
            empty_chat_zaglushka
        ),
        (null),
        modifier.fillMaxSize()
    )
}
