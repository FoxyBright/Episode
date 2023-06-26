package ru.rikmasters.gilty.chat.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.swiperefresh.*
import ru.rikmasters.gilty.bottomsheet.presentation.ui.reports.ReportAlert
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.*
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.PinnedBarType.TRANSLATION
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.*
import ru.rikmasters.gilty.chat.presentation.ui.chat.popUp.BottomBarMenu
import ru.rikmasters.gilty.chat.presentation.ui.chat.popUp.TopBarMenu
import ru.rikmasters.gilty.gallery.photoview.PhotoView
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.PHOTOS
import ru.rikmasters.gilty.shared.R.string.*
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.common.extentions.rememberDragRowState
import ru.rikmasters.gilty.shared.common.pagingPreview
import ru.rikmasters.gilty.shared.model.chat.*
import ru.rikmasters.gilty.shared.model.image.DemoThumbnailModel
import ru.rikmasters.gilty.shared.model.image.ThumbnailModel
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.shared.GAlert
import ru.rikmasters.gilty.shared.shared.PagingLoader
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun ChatPreview() {
    GiltyTheme {
        ChatContent(
            state = ChatState(
                ChatAppBarState(
                    name = "",
                    avatar = DemoAvatarModel,
                    memberCount = 10,
                    chatType = TRANSLATION,
                    viewer = null,
                    toTranslation = null,
                    isOnline = true,
                    isOrganizer = true
                ),
                answer = DemoMessageModel,
                meet = DemoMeetingModel,
                messageText = "",
                messageList = pagingPreview(DemoMessageModelList),
                userId = "",
                alert = false,
                meetAlert = false,
                kebabMenuState = false,
                messageMenuState = false,
                imageMenuState = false,
                listState = LazyListState(),
                unreadCount = 10,
                writingUsers = listOf(Pair("", DemoThumbnailModel))
            ),
            modifier = Modifier.background(colorScheme.background)
        )
    }
}

data class ChatState(
    val topState: ChatAppBarState,
    val answer: MessageModel?,
    val meet: MeetingModel,
    val messageText: String,
    val messageList: LazyPagingItems<MessageModel>,
    val userId: String,
    val alert: Boolean,
    val meetAlert: Boolean,
    val kebabMenuState: Boolean,
    val messageMenuState: Boolean,
    val imageMenuState: Boolean,
    val listState: LazyListState,
    val unreadCount: Int,
    val writingUsers: List<Pair<String, ThumbnailModel>>,

    val photoViewState: Boolean = false,
    val viewerImages: List<AvatarModel?> = emptyList(),
    val viewerSelectImage: AvatarModel? = null,
    val viewerMenuState: Boolean = false,
    val viewerType: PhotoViewType = PHOTOS,
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
    fun onImageMenuDismiss()
    fun onImageMenuItemSelect(point: Int)
    fun onDownButtonClick()
    fun onListDown()
    fun onMessageMenuItemSelect(
        point: Int,
        message: MessageModel,
    )
    
    fun onPhotoViewDismiss(state: Boolean)
    fun onPhotoViewChangeMenuState(state: Boolean) = Unit
    fun onPhotoViewMenuItemClick(imageId: String) = Unit
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatContent(
    state: ChatState,
    modifier: Modifier = Modifier,
    callback: ChatCallback? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ChatAppBarContent(
                state = state.topState,
                modifier = modifier,
                callback = callback
            )
        },
        bottomBar = {
            MessengerBar(
                text = state.messageText,
                modifier = Modifier.imePadding(),
                answer = state.answer,
                isOnline = state.meet.isOnline,
                callback = callback
            )
        },
        floatingActionButton = {
            ChatFloatingButton(
                listState = state.listState,
                unReadCount = state.unreadCount,
                isOnline = state.meet.isOnline,
                modifier = Modifier,
                onListDown = { callback?.onListDown() },
                onClick = { callback?.onDownButtonClick() }
            )
        },
        content = {
            Content(
                padding = it,
                state = state,
                modifier = Modifier,
                callback = callback
            )
        }
    )
    
    TopBarMenu(
        state = state.kebabMenuState,
        onDismiss = { callback?.onKebabClick() },
        onSelect = { callback?.onMenuItemClick(it) })
    
    BottomBarMenu(
        state = state.imageMenuState,
        onDismiss = { callback?.onImageMenuDismiss() },
        onSelect = { callback?.onImageMenuItemSelect(it) }
    )
    
    if(state.photoViewState) PhotoView(
        images = state.viewerImages,
        selected = state.viewerSelectImage,
        menuState = state.viewerMenuState,
        type = state.viewerType,
        onMenuClick = { callback?.onPhotoViewChangeMenuState(it) },
        onBack = { callback?.onPhotoViewDismiss(false) },
    )
    
    ReportAlert(state.alert) { callback?.closeAlert() }
    
    GAlert(
        show = state.meetAlert,
        onDismissRequest = {
            callback?.onMeetOutAlertDismiss()
        },
        success = Pair(stringResource(out_button)) {
            callback?.onMeetOut()
        },
        title = (stringResource(exit_from_meet) + "?"),
        label = stringResource(exit_from_meet_label),
        cancel = Pair(stringResource(cancel_button)) {
            callback?.onMeetOutAlertDismiss()
        }
    )
}

@Composable
private fun PreviewLazy() {
    val list =
        DemoMessageModelList.map { it to rememberDragRowState() }
    LazyColumn(Modifier.padding(horizontal = 16.dp)) {
        itemsIndexed(list) { index, (mes, row) ->
            ChatMessage(
                message = mes,
                state = row,
                sender = (index % 2 != 0),
                isOnline = true,
                last = if(
                    index < DemoMessageModelList.size.minus(1)
                ) list[index.plus(1)].first else null,
                next = if(
                    index in 1..DemoMessageModelList.size
                ) list[index.minus(1)].first else null
            )
        }
    }
}

@Composable
private fun Content(
    padding: PaddingValues,
    state: ChatState,
    modifier: Modifier = Modifier,
    callback: ChatCallback?,
) {
    val list = state.messageList
    
    if(LocalInspectionMode.current) PreviewLazy()
    else LazyColumn(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
            .background(colors.chatBack),
        state = state.listState,
        reverseLayout = true
    ) {
        when {
            state.messageList.loadState.refresh is
                    LoadState.Error -> {
            }
            state.messageList
                .loadState.append is
                    LoadState.Error -> {
            }
            else -> {
                item { Spacer(Modifier.height(28.dp)) }
                writingUsers(state.writingUsers)
                itemsIndexed(list) { index, item ->
                    
                    val messWithRow =
                        (item ?: MessageModel()) to rememberDragRowState()
                    
                    val userSender = messWithRow.first
                        .message?.author?.id == state.userId
                    
                    val lastMess by remember(list) {
                        mutableStateOf(
                            if(index < list.itemCount.minus(1)) {
                                list[index.plus(1)]
                            } else null
                        )
                    }
                    
                    val nextMess by remember(list) {
                        mutableStateOf(
                            if(index in 1..list.itemCount) {
                                list[index.minus(1)]
                            } else null
                        )
                    }
                    
                    ChatMessage(
                        message = messWithRow.first,
                        state = messWithRow.second,
                        sender = userSender,
                        isOnline = state.meet.isOnline,
                        last = lastMess,
                        next = nextMess,
                        callback = callback
                    )
                }
                if(state.messageList.loadState.append
                            is LoadState.Loading
                ) item { PagingLoader(list.loadState) }
            }
        }
    }
}

private fun LazyListScope.writingUsers(
    users: List<Pair<String, ThumbnailModel>>,
) {
    if(users.isNotEmpty()) item {
        Writing(
            list = users.map {
                it.second
            },
            modifier = Modifier
                .padding(16.dp, 2.dp)
        )
    }
}

@Composable
private fun Writing(
    list: List<ThumbnailModel>,
    modifier: Modifier = Modifier,
) {
    Row(modifier, Start, CenterVertically) {
        WritingMessage()
        list.forEachIndexed { i, it ->
            val mod = if(i == 0)
                Modifier.padding(start = 6.dp)
            else
                Modifier.offset((-16).dp)
            GCachedImage(
                it.url, mod
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
@Suppress("unused")
// TODO отслеживает - когда лист пролистывают вверх
private fun LazyListState.isScrollingUp(): Boolean {
    val firstIndex by remember {
        derivedStateOf { firstVisibleItemIndex }
    }
    val firstOffset by remember {
        derivedStateOf { firstVisibleItemScrollOffset }
    }
    var previousIndex by remember(this) { mutableStateOf(firstIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstOffset) }
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
