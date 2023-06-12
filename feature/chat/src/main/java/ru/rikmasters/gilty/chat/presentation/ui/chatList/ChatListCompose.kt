@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.chat.presentation.ui.chatList

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadState.NotLoading
import androidx.paging.compose.*
import ru.rikmasters.gilty.chat.presentation.ui.ChatListPlaceholder
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.AlertState.LIST
import ru.rikmasters.gilty.chat.presentation.ui.chatList.alert.ChatDeleteAlert
import ru.rikmasters.gilty.chat.viewmodel.ChatListViewModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.string.chats_ended_chats_label
import ru.rikmasters.gilty.shared.common.extentions.DragRowState
import ru.rikmasters.gilty.shared.common.extentions.rememberDragRowState
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.chat.SortTypeModel.MEETING_DATE
import ru.rikmasters.gilty.shared.model.chat.SortTypeModel.MESSAGE_DATE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.Colors.Red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ChatListPreview() {
    GiltyTheme {
        ChatListContent(
            state = ChatListState(
                stateList = listOf(
                    INACTIVE, INACTIVE, INACTIVE,
                    INACTIVE, ACTIVE
                ),
                chats = pagingPreview(DemoChatModelList),
                endedState = true,
                alertActive = false,
                alertState = LIST,
                alertSelect = 1,
                sortType = MEETING_DATE,
                listState = LazyListState(),
                isSortOn = false,
                isArchiveOn = false,
                chatsCount = 4,
            ),
            modifier = Modifier.background(
                colorScheme.background
            )
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
    val sortType: SortTypeModel?,
    val listState: LazyListState,
    val isSortOn: Boolean,
    val isArchiveOn: Boolean,
    val smthError: Boolean = false,
    val chatsCount: Int,
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
    fun onSortClick(sortTypeModel: SortTypeModel?)
    fun onArchiveClick()
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatListContent(
    state: ChatListState,
    modifier: Modifier = Modifier,
    callback: ChatListCallback? = null,
) {
    Scaffold(
        modifier = modifier.background(Transparent),
        topBar = { TopBar(Modifier) },
        bottomBar = {
            NavBar(state.stateList, Modifier) {
                callback?.onNavBarSelect(it)
            }
        },
        containerColor = Transparent,
        content = {
            if (!state.smthError)
                Column(Modifier.padding(it)) {
                    Use<ChatListViewModel>(PullToRefreshTrait) {
                        Content(state, Modifier, callback)
                    }
                } else ErrorInternetConnection {
                callback?.onListUpdate()
            }
        }
    )
    ChatDeleteAlert(
        active = state.alertActive,
        state = state.alertState,
        select = state.alertSelect,
        listItemSelect = { callback?.onListAlertSelect(it) },
        onDismiss = { callback?.onAlertDismiss() },
        onSuccess = { callback?.onAlertSuccess() }
    )
}

@Composable
private fun TopBar(
    modifier: Modifier,
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
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    state: ChatListState,
    modifier: Modifier = Modifier,
    callback: ChatListCallback?,
) {
    val chats = state.chats
    val itemCount = state.chatsCount

    if (LocalInspectionMode.current)
        PreviewLazy() else {
        Column(modifier = modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                state = state.listState
            ) {
                when {
                    chats.loadState.refresh is LoadState.Error -> Unit
                    chats.loadState.append is LoadState.Error -> Unit
                    else -> {
                        if (chats.loadState.refresh is LoadState.Loading || chats.loadState.append is LoadState.Loading)
                            item { PagingLoader(chats.loadState) }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (itemCount != 0) {

                            item {
                                SortTypeLabels(
                                    modifier = Modifier.animateItemPlacement(),
                                    state = state,
                                    callback = callback
                                )
                            }

                            if ((state.sortType == null && !state.isArchiveOn) || state.sortType == MEETING_DATE) { // No sort or Meeting Date
                                getSortedByTimeChats(state.chats.itemSnapshotList.items).let {
                                    if (it.isNotEmpty())
                                        items(it) { (label, list) ->
                                            Label(
                                                label,
                                                Modifier
                                                    .padding(
                                                        top = 28.dp,
                                                        bottom = 18.dp
                                                    )
                                                    .padding(horizontal = 16.dp)
                                                    .animateItemPlacement()
                                            )
                                            list.map { chatModel ->
                                                chatModel to rememberDragRowState()
                                            }.forEachIndexed { index, chat ->
                                                ElementChat(
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp)
                                                        .animateItemPlacement(),
                                                    chat.first, index, list.size,
                                                    chat.second, callback
                                                )
                                            }
                                        }
                                }
                            } else if (state.sortType == MESSAGE_DATE && state.chats.loadState.append is NotLoading) { // Message Date
                                if (state.chats.itemSnapshotList.isNotEmpty())
                                    item {
                                        Spacer(Modifier.height(28.dp))
                                    }

                                itemsIndexed(state.chats) { index, item ->
                                    val chat = (item) to rememberDragRowState()
                                    chat.first?.let {
                                        ElementChat(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .animateItemPlacement(),
                                            chat = it,
                                            index = (index),
                                            size = (state.chats.itemCount),
                                            rowState = (chat.second),
                                            callback = callback
                                        )
                                    }
                                }
                            } else { // Archieve
                                if (state.chats.itemSnapshotList.isNotEmpty())
                                    item {
                                        Spacer(Modifier.height(28.dp))
                                    }

                                itemsIndexed(state.chats) { index, item ->
                                    val chat = (item) to rememberDragRowState()
                                    chat.first?.let {
                                        ElementChat(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .animateItemPlacement(),
                                            chat = it,
                                            index = index,
                                            size = state.chats.itemCount,
                                            rowState = chat.second,
                                            callback = callback
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
            val emptyChats = chats.loadState.append is NotLoading
                    && chats.loadState.refresh is NotLoading
                    && (itemCount == 0)

            if (emptyChats) ChatListPlaceholder(
                modifier = Modifier.padding(bottom = 120.dp)
            )
        }
    }
}

@Composable
private fun PreviewLazy() {
    DemoChatModelList.map {
        it to rememberDragRowState()
    }.let { list ->
        LazyColumn(
            Modifier.padding(
                horizontal = 16.dp
            )
        ) {
            item {
                Label(
                    stringResource(R.string.chats_unread)
                )
            }
            itemsIndexed(list) { i, chat ->
                ElementChat(
                    modifier = Modifier,
                    chat.first, i,
                    list.size,
                    chat.second
                )
            }
        }
    }
}

@Composable
private fun ElementChat(
    modifier: Modifier = Modifier,
    chat: ChatModel,
    index: Int, size: Int,
    rowState: DragRowState,
    callback: ChatListCallback? = null,
) {
    val shape = lazyItemsShapes(index, size)
    Column(
        modifier.background(
            colorScheme.primaryContainer,
            shape
        )
    ) {
        SwipeableChatRow(
            rowState, chat, shape, Modifier,
            { callback?.onChatClick(it) }
        ) { callback?.onChatSwipe(it) }
        if (index < size - 1) GDivider(
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
            (null), Modifier.size(28.dp),
            colorScheme.tertiary
        )
    }
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier = Modifier,
) = Text(
    text,
    modifier,
    colorScheme.tertiary,
    style = typography.labelLarge
)

@Composable
fun SortTypeLabels(
    modifier: Modifier = Modifier,
    state: ChatListState,
    callback: ChatListCallback?,
) {
    var sortLabelHeightDp by remember { mutableStateOf(0.dp) }
    val localDensity = LocalDensity.current
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.animateContentSize()) {
            state.sortType?.let {
                GChip(
                    modifier = Modifier.padding(
                        start = if (sortLabelHeightDp - 16.dp >= 0.dp)
                            sortLabelHeightDp - 16.dp
                        else 0.dp
                    ),
                    text = it.getSortName(),
                    isSelected = true,
                    primary = Red
                ) {}
            }
            Row(modifier = Modifier.onGloballyPositioned { coordinates ->
                sortLabelHeightDp =
                    with(localDensity) { coordinates.size.width.toDp() }
            }, verticalAlignment = CenterVertically) {
                if (state.sortType != null) {
                    Image(
                        painter = painterResource(
                            if (isSystemInDarkTheme())
                                R.drawable.ic_close_sort_dark
                            else R.drawable.ic_close_sort
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(26.dp)
                            .clickable {
                                callback?.onSortClick(
                                    null
                                )
                            },
                    )
                }
                GChip(
                    text = stringResource(R.string.chats_sort_label),
                    isSelected = state.sortType != null
                ) { callback?.onSortClick(MEETING_DATE) }
            }
        }
        if (state.sortType != null)
            GChip(
                modifier = Modifier.padding(start = 8.dp),
                text = (if (state.sortType == MEETING_DATE)
                    MESSAGE_DATE else MEETING_DATE).getSortName()
            ) {
                callback?.onSortClick(
                    if (state.sortType == MEETING_DATE)
                        MESSAGE_DATE else MEETING_DATE
                )
            }

        if (state.sortType == null)
            GChip(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = R.string.chats_archive_label),
                isSelected = state.isArchiveOn
            ) { callback?.onArchiveClick() }
    }
}

