package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.google.accompanist.swiperefresh.SwipeRefreshState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.vibrate

@Composable
fun LoadingIndicator(
    swipeRefreshState: SwipeRefreshState,
    offset: Dp,
    trigger: Dp,
) {
    val context = LocalContext.current
    
    val size = remember(offset) { min(offset, trigger) }
    
    LaunchedEffect(size == trigger) {
        if(size == trigger)
            vibrate(context)
    }
    
    Box(Modifier, Center) {
        AnimatedImage(
            R.raw.loaging,
            Modifier.size(size),
            isPlaying = swipeRefreshState.isRefreshing
        )
    }
}

@Composable
fun PagingLoader(state: CombinedLoadStates) {
    when(state.append) {
        is LoadState.NotLoading -> Unit
        LoadState.Loading -> PaginatorIndicator()
        is LoadState.Error -> Text("")
    }
    when(state.refresh) {
        is LoadState.NotLoading -> Unit
        LoadState.Loading -> PaginatorIndicator()
        is LoadState.Error -> Text("")
    }
}

@Composable
private fun PaginatorIndicator() {
    Box(Modifier.fillMaxWidth(), Center) {
        AnimatedImage(
            R.raw.loaging,
            Modifier.size(40.dp),
        )
    }
}