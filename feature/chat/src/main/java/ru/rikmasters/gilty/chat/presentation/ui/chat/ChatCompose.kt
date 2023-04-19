package ru.rikmasters.gilty.chat.presentation.ui.chat

import android.util.Log
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
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.*
import ru.rikmasters.gilty.bottomsheet.presentation.ui.reports.ReportAlert
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.*
import ru.rikmasters.gilty.chat.presentation.ui.chat.bars.PinnedBarType.TRANSLATION
import ru.rikmasters.gilty.chat.presentation.ui.chat.message.*
import ru.rikmasters.gilty.gallery.photoview.PhotoView
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.PHOTO
import ru.rikmasters.gilty.shared.R.string.*
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
                    name = "Название встречи",
                    avatar = DemoAvatarModel,
                    memberCount = 10,
                    chatType = TRANSLATION,
                    viewer = null,
                    toTranslation = null,
                    (true),
                    (true)
                ),
                answer = DemoMessageModel,
                meet = DemoMeetingModel,
                messageText = "Вводимое сообщение",
                messageList = pagingPreview(DemoMessageModelList),
                userId = "id",
                alert = false,
                meetAlert = false,
                kebabMenuState = false,
                messageMenuState = false,
                imageMenuState = false,
                listState = LazyListState(),
                unReadCount = 10,
                writingUsers = listOf(Pair("id", DemoThumbnailModel))
            ),
            modifier = Modifier.background(
                colorScheme.background
            )
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
    val unReadCount: Int,
    val writingUsers: List<Pair<String, ThumbnailModel>>,
    
    val photoViewState: Boolean = false,
    val viewerImages: List<AvatarModel?> = emptyList(),
    val viewerSelectImage: AvatarModel? = null,
    val viewerMenuState: Boolean = false,
    val viewerType: PhotoViewType = PHOTO,
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
        modifier,
        topBar = {
            ChatTopBar(
                state.topState,
                state.kebabMenuState,
                Modifier,
                callback
            )
        },
        bottomBar = {
            ChatBottomBar(
                state.imageMenuState,
                state.messageText,
                state.answer,
                state.meet.isOnline,
                Modifier,
                callback
            )
        },
        floatingActionButton = {
            ChatFloatingButton(
                state.listState,
                state.unReadCount,
                state.meet.isOnline,
                Modifier,
                { callback?.onListDown() }
            ) { callback?.onDownButtonClick() }
        },
        content = {
            Content(
                it, state,
                Modifier, callback
            )
        }
    )
    
    if(state.photoViewState) PhotoView(
        images = state.viewerImages,
        selected = state.viewerSelectImage,
        menuState = state.viewerMenuState,
        type = state.viewerType,
        onMenuClick = { callback?.onPhotoViewChangeMenuState(it) },
        onMenuItemClick = { callback?.onPhotoViewMenuItemClick(it) },
        onBack = { callback?.onPhotoViewDismiss(false) },
    )
    
    ReportAlert(state.alert) { callback?.closeAlert() }
    
    GAlert(
        show = state.meetAlert,
        modifier = Modifier,
        onDismissRequest = { callback?.onMeetOutAlertDismiss() },
        success = Pair(stringResource(out_button)) { callback?.onMeetOut() },
        title = (stringResource(exit_from_meet) + "?"),
        label = stringResource(exit_from_meet_label),
        cancel = Pair(stringResource(cancel_button)) { callback?.onMeetOutAlertDismiss() }
    )
}

@Composable
private fun PreviewLazy() {
    val list =
        DemoMessageModelList.map { it to rememberDragRowState() }
    LazyColumn(Modifier.padding(horizontal = 16.dp)) {
        itemsIndexed(list) { index, (mes, row) ->
            ChatMessage(
                mes, row, (index % 2 != 0), (true),
                if(index < DemoMessageModelList.size.minus(1)) {
                    list[index.plus(1)].first
                } else null,
                if(index in 1..DemoMessageModelList.size) {
                    list[index.minus(1)].first
                } else null
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
        modifier
            .padding(padding)
            .fillMaxSize()
            .background(colors.chatBack),
        state.listState,
        reverseLayout = true
    ) {
        when {
            state.messageList.loadState.refresh is LoadState.Error -> {}
            state.messageList.loadState.append is LoadState.Error -> {}
            else -> {
                item { Spacer(Modifier.height(28.dp)) }
                
                if(state.writingUsers.isNotEmpty()) item {
                    Writing(
                        state.writingUsers.map { it.second },
                        Modifier.padding(16.dp, 2.dp)
                    )
                }
                itemsIndexed(list) { index, item ->
                    ((item
                        ?: MessageModel()) to rememberDragRowState()).let { (mes, row) ->
                        ChatMessage(
                            mes, row,
                            (mes.message?.author?.id == state.userId),
                            state.meet.isOnline,
                            if(index < list.itemCount.minus(1)) {
                                list[index.plus(1)]
                            } else null,
                            if(index in 1..list.itemCount) {
                                list[index.minus(1)]
                            } else null,
                            callback
                        )
                    }
                }
                if(state.messageList.loadState.append is LoadState.Loading) {
                    Log.d("TEST", "append")
                    item {
                        PagingLoader(list.loadState)
                    }
                }
            }
        }
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
            val mod = if(i == 0) {
                Modifier.padding(start = 6.dp)
            } else Modifier.offset((-16).dp)
            AsyncImage(
                it.url,
                (null),
                mod
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
@Suppress("unused")
/*TODO отслеживает - когда лист пролистывают вверх
   (понадобится для регулирования плавающей кнопки)
*/
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
