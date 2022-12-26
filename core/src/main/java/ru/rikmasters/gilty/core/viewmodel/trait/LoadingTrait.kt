package ru.rikmasters.gilty.core.viewmodel.trait

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.StateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

/**
 * Включает загрузку по умолчанию
 */
interface LoadingTrait: Trait {
    companion object: TraitWrapperFactory() {
        @Composable
        override fun <VM: ViewModel> Wrapper(vm: VM, content: @Composable (VM) -> Unit) {
            
            val loading by vm.loading.collectAsState()
    
            (loader ?: defaultLoader)(loading) { content(vm) }
        }
        
        private val defaultLoader = @Composable { isLoading: Boolean, content: @Composable () -> Unit ->
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
                ){
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center),
                        MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        var loader: (@Composable (isLoading: Boolean, content: @Composable () -> Unit) -> Unit)? = null
    }
    
    val loading: StateFlow<Boolean>
}