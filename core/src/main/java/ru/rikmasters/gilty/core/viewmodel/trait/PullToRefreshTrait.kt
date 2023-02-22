package ru.rikmasters.gilty.core.viewmodel.trait

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.core.viewmodel.ViewModel

interface PullToRefreshTrait: LoadingTrait {
    companion object: TraitWrapperFactory() {
        
        @Composable
        override fun <VM: ViewModel> Wrapper(vm: VM, content: @Composable (VM) -> Unit) {
            if(vm !is PullToRefreshTrait) {
                content(vm)
                return
            }
            
            val currentTrigger = remember(trigger) { trigger ?: defaultTrigger }
            
            val isLoading by vm.loading.collectAsState()
            var isPullTrigger by remember { mutableStateOf(false) }
            
            val isRefreshing = remember(isLoading) {
                if(!isLoading) isPullTrigger = false
                isLoading && isPullTrigger
            }
            
            val state = rememberSwipeRefreshState(isRefreshing)
            val offset = with(LocalDensity.current) { state.indicatorOffset.toDp() }
            
            val animatedOffset by animateDpAsState(
                if(isRefreshing) currentTrigger
                else if(!isRefreshing && !state.isSwipeInProgress)
                    0.dp
                else offset,
                if(state.isSwipeInProgress) snap()
                else spring(visibilityThreshold = Dp.VisibilityThreshold)
            )
            
            val scope = rememberCoroutineScope()
            
            SwipeRefresh(
                state = state,
                refreshTriggerDistance = currentTrigger,
                onRefresh = {
                    isPullTrigger = true
                    scope.launch {
                        vm.forceRefresh()
                    }
                },
                indicator = { swipeState, trigger ->
                    (indicator ?: defaultIndicator)(swipeState, animatedOffset, trigger)
                },
                content = {
                    Box(Modifier.offset(y = animatedOffset)) {
                        if(isLoading && !isRefreshing) LoadingTrait.Wrapper(vm) {
                            content(vm)
                        } else
                            content(vm)
                    }
                }
            )
        }
        
        private val defaultTrigger = 72.dp
        var trigger: Dp? = null
        
        private val defaultIndicator = @Composable { state: SwipeRefreshState, _: Dp, trigger: Dp ->
            SwipeRefreshIndicator(state, trigger)
        }
        var indicator: (@Composable (SwipeRefreshState, Dp, Dp) -> Unit)? = null
    }
    
    suspend fun forceRefresh()
}