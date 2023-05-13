package ru.rikmasters.gilty.bottomsheet.presentation.ui.responds

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import ru.rikmasters.gilty.shared.shared.GiltyTabElement
import ru.rikmasters.gilty.shared.shared.PagingLoader
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

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
        CustomIndicator(tabPositions, pagerState)
    }


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
                TabRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .border(0.5.dp, colorScheme.scrim, CircleShape),
                    contentColor = Color.White,
                    selectedTabIndex = pagerState.currentPage,
                    indicator = indicator,
                    containerColor = Color.Transparent
                ) {
                    listOf(
                        stringResource(R.string.profile_sent_responds),
                        stringResource(R.string.profile_received_responds)
                    ).forEachIndexed { index, title ->
                        GiltyTabElement(
                            title,
                            Modifier
                                .weight(1f)
                                .zIndex(2f)
                                .border(if(pagerState.currentPage == 1) 0.5.dp else 0.dp, colorScheme.scrim,
                                    RoundedCornerShape(0.dp)
                                ),
                            (pagerState.currentPage == index), false,
                            !listOf(
                                stringResource(R.string.profile_sent_responds),
                                stringResource(R.string.profile_received_responds)
                            ).contains(title)
                        ) {
                            scope.launch {  pagerState.animateScrollToPage(index) }
                        }
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    count = 2
                ) { selectTab: Int ->
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

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CustomIndicator(
    tabPositions: List<androidx.compose.material3.TabPosition>,
    pagerState: PagerState
) {
    val transition = updateTransition(pagerState.currentPage, label = "")
    val indicatorStart by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 50f)
            } else {
                spring(dampingRatio = 1f, stiffness = 1000f)
            }
        }, label = ""
    ) {
        tabPositions[it].left
    }

    val indicatorEnd by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 1000f)
            } else {
                spring(dampingRatio = 1f, stiffness = 50f)
            }
        }, label = ""
    ) {
        tabPositions[it].right
    }

    Box(
        Modifier
            .offset(x = indicatorStart)
            .wrapContentSize(align = Alignment.BottomStart)
            .width(indicatorEnd - indicatorStart)
            .fillMaxSize()
            .background(
                color = colors.tabActive,
                if (pagerState.currentPage == 0) RoundedCornerShape(
                    topStart = 50.dp,
                    bottomStart = 50.dp
                ) else RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
            )
            .zIndex(1f)
    )

}
