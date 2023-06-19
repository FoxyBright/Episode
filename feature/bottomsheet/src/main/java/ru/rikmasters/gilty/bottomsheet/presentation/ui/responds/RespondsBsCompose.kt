package ru.rikmasters.gilty.bottomsheet.presentation.ui.responds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.FULL
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.SHORT
import ru.rikmasters.gilty.gallery.photoview.PhotoView
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.LOAD
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.*
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModelWithPhotos
import ru.rikmasters.gilty.shared.model.notification.RespondWithPhotos
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.*

enum class RespondsBsType { MEET, FULL, SHORT }

data class RespondsListState(
    val type: RespondsBsType,
    val responds: LazyPagingItems<RespondWithPhotos>,
    val localResponds: List<Pair<Boolean, RespondWithPhotos>>,
    val sentResponds: LazyPagingItems<MeetWithRespondsModelWithPhotos>,
    val localSentResponds: List<Pair<Boolean, MeetWithRespondsModelWithPhotos>>,
    val receivedResponds: LazyPagingItems<MeetWithRespondsModelWithPhotos>,
    val localReceivedResponds: List<Pair<Boolean, MeetWithRespondsModelWithPhotos>>,
    val respondsStates: List<Int>,
    val photoViewState: Boolean = false,
    val viewerImages: List<AvatarModel?> = emptyList(),
    val viewerSelectImage: AvatarModel? = null,
    val viewerMenuState: Boolean = false,
    val scope: CoroutineScope,
    val currentTab: Int = 0,
)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun RespondsList(
    modifier: Modifier = Modifier,
    state: RespondsListState,
    callback: RespondsListCallback? = null,
) {
    val pagerState: PagerState =
        rememberPagerState(initialPage = state.currentTab)
    val indicator =
        @Composable { tabPositions: List<TabPosition> ->
            TabIndicator(tabPositions, pagerState)
        }

    LaunchedEffect(pagerState.currentPage) {
        callback?.onTabChange(pagerState.currentPage)
    }

    LaunchedEffect(key1 = state.currentTab, block = {
        pagerState.animateScrollToPage(state.currentTab)
    })

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        TopBar(
            type = state.type,
            Modifier.padding(
                top = 28.dp,
                bottom = if (state.type == FULL) {
                    16.dp
                } else 8.dp
            )
        ) { callback?.onBack() }
        when (state.type) {
            MEET -> MeetResponds(
                modifier = Modifier.padding(vertical = 8.dp),
                responds = state.responds,
                callback = callback
            )

            FULL -> {
                GiltyPagerTab(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .border(0.5.dp, colorScheme.scrim, CircleShape),
                    selectedTabIndex = pagerState.currentPage,
                    indicator = indicator,
                    titles = listOf(
                        stringResource(R.string.profile_sent_responds),
                        stringResource(R.string.profile_received_responds)
                    ),
                    onTabClick = { index ->
                        state.scope.launch { pagerState.animateScrollToPage(index) }
                    }
                )

                HorizontalPager(
                    state = pagerState,
                    count = 2,
                    itemSpacing = 12.dp,
                ) { selectTab: Int ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (selectTab == 0)
                            SentResponds(
                                modifier = Modifier.padding(vertical = 8.dp),
                                responds = state.sentResponds,
                                localResponds = state.localSentResponds,
                                callback = callback
                            )
                        else
                            ReceivedResponds(
                                modifier = Modifier.padding(vertical = 8.dp),
                                responds = state.receivedResponds,
                                respondsStates = state.respondsStates,
                                callback = callback
                            )
                    }
                }
            }

            SHORT -> ReceivedResponds(
               modifier = Modifier.padding(vertical = 8.dp),
                responds = state.receivedResponds,
                respondsStates = state.respondsStates,
                callback = callback
            )
        }
    }
    if (state.photoViewState) PhotoView(
        images = state.viewerImages,
        selected = state.viewerSelectImage,
        menuState = state.viewerMenuState,
        type = LOAD,
        onMenuClick = { callback?.onPhotoViewChangeMenuState(it) },
        onMenuItemClick = { callback?.onPhotoViewMenuItemClick(it) },
        onBack = { callback?.onPhotoViewDismiss(false) },
    )
}

@Composable
private fun SentResponds(
    modifier: Modifier = Modifier,
    responds: LazyPagingItems<MeetWithRespondsModelWithPhotos>,
    localResponds: List<Pair<Boolean, MeetWithRespondsModelWithPhotos>>,
    callback: RespondsListCallback? = null,
) {
    when {
        responds.loadState.refresh is LoadState.Error -> {}
        responds.loadState.append is LoadState.Error -> {}
        responds.loadState.refresh is LoadState.Loading -> {
            PagingLoader(responds.loadState)
        }

        else -> {
            LazyColumn(modifier.fillMaxWidth()) {
                val itemCount = responds.itemCount
                if (itemCount != 0) {
                    /*itemsIndexed(items = responds, key = {index, item -> item.id}){ index, item ->
                        if(index < localResponds.size && !localResponds[index].first){
                            sentRespond(
                                meetId = it.id,
                                name = it.tags.joinToString(
                                    separator = ", "
                                ) { t -> t.title },
                                organizer = it.organizer,
                                responds = it.responds,
                                modifier = Modifier
                                    .padding(bottom = 12.dp),
                                callback = callback
                            )
                        }
                    }*/
                    /*localResponds.forEach { item: Pair<Boolean, MeetWithRespondsModelWithPhotos> ->

                        sentRespond(
                            meetId = it.id,
                            name = it.tags.joinToString(
                                separator = ", "
                            ) { t -> t.title },
                            organizer = it.organizer,
                            responds = it.responds,
                            modifier = Modifier
                                .padding(bottom = 12.dp),
                            callback = callback
                        )
                    }*/
                    //Original
                    responds.itemSnapshotList.items.forEach {
                        sentRespond(
                            meetId = it.id,
                            name = it.tags.joinToString(
                                separator = ", "
                            ) { t -> t.title },
                            organizer = it.organizer,
                            responds = it.responds,
                            modifier = Modifier
                                .padding(bottom = 12.dp),
                            callback = callback
                        )
                    }

                    if (responds.loadState.append is LoadState.Loading) {
                        item { PagingLoader(responds.loadState) }
                    }
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .background(
                                    color = colorScheme.background
                                )
                        )
                    }
                } else if (responds.loadState.refresh is LoadState.NotLoading) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize()) {
                            EmptyScreen(
                                stringResource(
                                    R.string.profile_hasnt_sent_responds
                                ),
                                R.drawable.broken_heart
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MeetResponds(
    modifier: Modifier = Modifier,
    responds: LazyPagingItems<RespondWithPhotos>,
    callback: RespondsListCallback? = null,
) {
    when {
        responds.loadState.refresh is LoadState.Error -> {}
        responds.loadState.append is LoadState.Error -> {}
        responds.loadState.refresh is LoadState.Loading -> {
            PagingLoader(responds.loadState)
        }

        else -> {
            LazyColumn(modifier.fillMaxWidth()) {
                val itemCount = responds.itemCount
                if (itemCount != 0) {
                    items(responds) {
                        it?.let {
                            ReceivedRespond(
                                it,
                                Modifier
                                    .padding(bottom = 6.dp),
                                callback
                            )
                        }
                    }
                    if (responds.loadState.append is LoadState.Loading) {
                        item { PagingLoader(responds.loadState) }
                    }
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .background(
                                    color = colorScheme.background
                                )
                        )
                    }
                } else {
                    if (responds.loadState.refresh is LoadState.NotLoading) {
                        item {
                            Box(modifier = Modifier.fillParentMaxSize()) {
                                EmptyScreen(
                                    stringResource(
                                        R.string.profile_hasnt_received_responds
                                    ),
                                    R.drawable.broken_heart
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceivedResponds(
    modifier: Modifier = Modifier,
    responds: LazyPagingItems<MeetWithRespondsModelWithPhotos>,
    respondsStates: List<Int>,
    callback: RespondsListCallback? = null,
) {
    RespondsListContent(
        state = RespondsListState(responds, respondsStates),
        modifier = modifier,
        callback = callback
    )
}

@Composable
private fun TopBar(
    type: RespondsBsType,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    Row(
        modifier.fillMaxWidth(),
        SpaceBetween,
        CenterVertically
    ) {
        Row(
            Modifier.weight(1f),
            Start,
            CenterVertically
        ) {
            if (type == MEET) IconButton(
                { onBack() },
                Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_back),
                    (null),
                    Modifier,
                    colorScheme.tertiary
                )
            }
            Text(
                stringResource(R.string.profile_responds_label),
                Modifier,
                colorScheme.tertiary,
                style = typography.labelLarge,
                overflow = Ellipsis,
                maxLines = 1
            )
        }
    }
}