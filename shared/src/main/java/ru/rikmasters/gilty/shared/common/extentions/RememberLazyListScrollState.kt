package ru.rikmasters.gilty.shared.common.extentions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect

private val SaveMap =
    mutableMapOf<String, Pair<Int, Int>>()

@Composable
fun rememberLazyListScrollState(
    key: String,
) = SaveMap[key].let { state ->
    val listState: LazyListState = rememberLazyListState(
        (state?.first ?: 0),
        (state?.second ?: 0)
    )
    LaunchedEffect(key1 = Unit, block = {
        listState.animateScrollToItem((state?.first ?: 0),(state?.second ?: 0).toInt())
    })
    listState
}.let {
    DisposableEffect(Unit) {
        onDispose {
            val index = it.firstVisibleItemIndex
            val offset = it.firstVisibleItemScrollOffset
            SaveMap[key] = index to offset
        }
    }; it
}

suspend fun LazyListState.animateToLastPosition(key:String){
    SaveMap[key].let { state->
        this.scrollToItem((state?.first ?: 0),(state?.second ?: 0).toInt())
    }
}