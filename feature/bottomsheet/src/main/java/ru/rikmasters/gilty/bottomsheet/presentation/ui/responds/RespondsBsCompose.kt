package ru.rikmasters.gilty.bottomsheet.presentation.ui.responds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
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
import ru.rikmasters.gilty.shared.shared.EmptyScreen
import ru.rikmasters.gilty.shared.shared.GiltyPagerTab
import ru.rikmasters.gilty.shared.shared.PagingLoader
import ru.rikmasters.gilty.shared.shared.TabIndicator

enum class RespondsBsType { MEET, FULL, SHORT }

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RespondsList(
    type: RespondsBsType,
    responds: LazyPagingItems<RespondWithPhotos>,
    meetResponds: LazyPagingItems<MeetWithRespondsModelWithPhotos>,
    respondsStates: List<Int>,
    selectTab: Int,
    modifier: Modifier = Modifier,
    photoViewState: Boolean = false,
    viewerImages: List<AvatarModel?> = emptyList(),
    viewerSelectImage: AvatarModel? = null,
    viewerMenuState: Boolean = false,
    scope:CoroutineScope,
    callback: RespondsListCallback? = null,
) {
    val pagerState: PagerState = rememberPagerState(initialPage = 0)
    val indicator = @Composable { tabPositions: List<androidx.compose.material3.TabPosition> ->
        TabIndicator(tabPositions, pagerState)
    }
    LaunchedEffect(key1 = pagerState.currentPage, block = {
        callback?.onTabChange(pagerState.currentPage)
    })


    Column(
        modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        TopBar(
            type,
            Modifier.padding(
                top = 28.dp,
                bottom = if (type == FULL) {
                    16.dp
                } else 8.dp
            )
        ) { callback?.onBack() }
        when (type) {
            MEET -> MeetResponds(
                Modifier.padding(vertical = 8.dp),
                responds,
                callback
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
                    onTabClick = {index->
                        scope.launch {  pagerState.animateScrollToPage(index) }

                    }
                )

                HorizontalPager(
                    state = pagerState,
                    count = 2
                ) { selectTab: Int ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (selectTab == 0) {
                            SentResponds(
                                Modifier.padding(vertical = 8.dp),
                                meetResponds,
                                callback
                            )
                        } else ReceivedResponds(
                            Modifier.padding(vertical = 8.dp),
                            meetResponds,
                            respondsStates,
                            callback
                        )
                    }
                }

            }

            SHORT -> ReceivedResponds(
                Modifier.padding(vertical = 8.dp),
                meetResponds,
                respondsStates,
                callback
            )
        }
    }
    if (photoViewState) PhotoView(
        images = viewerImages,
        selected = viewerSelectImage,
        menuState = viewerMenuState,
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
                    responds.itemSnapshotList.items.forEach {
                        sentRespond(
                            it.tags.joinToString(separator = ", ") { t -> t.title },
                            it.organizer,
                            it.responds,
                            Modifier.padding(bottom = 12.dp),
                            callback
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
        RespondsListState(responds, respondsStates),
        modifier,
        callback
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