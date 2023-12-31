package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModelWithPhotos
import ru.rikmasters.gilty.shared.model.notification.RespondWithPhotos
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.EmptyScreen
import ru.rikmasters.gilty.shared.shared.PagingLoader
import ru.rikmasters.gilty.shared.shared.RowActionBar

/*
@Preview
@Composable
private fun ReceivedResponds() {
    GiltyTheme {
        RespondsListContent(
            RespondsListState(
                listOf(
                    DemoMeetWithRespondsModelWithPhotos,
                    DemoMeetWithRespondsModelWithPhotos
                ), listOf(1)
            ), Modifier
                .background(colorScheme.background)
                .padding(16.dp)
        )
    }
}

 */

data class RespondsListState(
    val responds: LazyPagingItems<MeetWithRespondsModelWithPhotos>,
    val groupStates: List<Int>,
)

interface RespondsListCallback {
    
    fun onAcceptClick(respondId: String)
    fun onCancelClick(respondId: String)
    fun onRespondClick(
        authorId: String = "",
        meetId: String = ""
    )
    
    fun onImageClick(image: AvatarModel)
    fun onArrowClick(index: Int)
    fun onTabChange(tab: Int) {}
    fun onBack() {}
    fun onPhotoViewDismiss(state: Boolean)
    fun onPhotoViewChangeMenuState(state: Boolean) = Unit
    fun onPhotoViewMenuItemClick(imageId: String) = Unit
}

@Composable
fun RespondsListContent(
    state: RespondsListState,
    modifier: Modifier = Modifier,
    callback: RespondsListCallback? = null,
) {
    when {
        state.responds.loadState.refresh is LoadState.Error -> {}
        state.responds.loadState.append is LoadState.Error -> {}
        state.responds.loadState.refresh is LoadState.Loading -> {
            PagingLoader(state.responds.loadState)
        }
        else -> {
            LazyColumn(modifier) {
                val itemCount = state.responds.itemCount
                if(itemCount != 0) {
                    itemsIndexed(state.responds) { index, respond: MeetWithRespondsModelWithPhotos? ->
                        respond?.let {
                            GroupList(
                                index,
                                respond.tags.first().title,
                                respond.responds,
                                Modifier,
                                !(state.groupStates.contains(index)),
                                callback
                            )
                        }
                    }
                    if(state.responds.loadState.append is LoadState.Loading) {
                        item { PagingLoader(state.responds.loadState) }
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
                    if(state.responds.loadState.refresh is LoadState.NotLoading) {
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
private fun GroupList(
    index: Int,
    title: String,
    responds: List<RespondWithPhotos>,
    modifier: Modifier = Modifier,
    state: Boolean = false,
    callback: RespondsListCallback?,
) {
    Column(modifier) {
        Row(verticalAlignment = CenterVertically) {
            RowActionBar(
                title = title,
                modifier = Modifier.clickable(
                    MutableInteractionSource(),
                    (null)
                ) {
                    callback?.onArrowClick(index)
                },
                details = responds.size.toString()
            )
            IconButton({ callback?.onArrowClick(index) }) {
                Icon(
                    if(state) Filled.KeyboardArrowDown
                    else Filled.KeyboardArrowRight,
                    (null), Modifier.size(28.dp),
                    colorScheme.onTertiary
                )
            }
        }
        if(state) Column {
            responds.forEach {
                ReceivedRespond(
                    respond = it,
                    modifier = Modifier.padding(bottom = 6.dp),
                    callback = callback
                )
            }
        }
    }
}
