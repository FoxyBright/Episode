package ru.rikmasters.gilty.core.viewmodel.trait

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

/**
 * Включает загрузку по умолчанию
 */
interface LoadingTrait: Trait {
    
    companion object: TraitWrapperFactory() {
        
        @Composable
        override fun <VM: ViewModel> Wrapper(vm: VM, content: @Composable (VM) -> Unit) {
            
            var showLoader by remember { mutableStateOf(false) }
            
            val loading by vm.loading.collectAsState()
            
            LaunchedEffect(loading) {
                showLoader = if(loading) {
                    delay(500)
                    true
                } else
                    false
            }
            
            if(showLoader)
                (loader ?: defaultLoader)(loading) { content(vm) }
            else
                content(vm)
        }
        
        private val defaultLoader =
            @Composable { isLoading: Boolean, content: @Composable () -> Unit ->
                val alpha by animateFloatAsState(
                    if(isLoading) 1f else 0f
                )
                Box {
                    content()
                    if(alpha < 1E-6) Box(
                        Modifier
                            .fillMaxSize()
                            .alpha(alpha)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        CircularProgressIndicator(
                            Modifier.align(Alignment.Center),
                            MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        
        var loader: (@Composable (isLoading: Boolean, content: @Composable () -> Unit) -> Unit)? =
            null
    }
    
    val loading: StateFlow<Boolean>
    val pagingPull: StateFlow<Boolean>
}