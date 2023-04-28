package ru.rikmasters.gilty.shared.common.extentions

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

private val SaveMap =
    mutableMapOf<String, Pair<Int, Int>>()

@Composable
fun rememberLazyListScrollState(
    key: String,
) = SaveMap[key].let { state ->
    rememberLazyListState(
        (state?.first ?: 0),
        (state?.second ?: 0)
    )
}.let {
    DisposableEffect(Unit) {
        onDispose {
            val index = it.firstVisibleItemIndex
            val offset = it.firstVisibleItemScrollOffset
            SaveMap[key] = index to offset
        }
    }; it
}