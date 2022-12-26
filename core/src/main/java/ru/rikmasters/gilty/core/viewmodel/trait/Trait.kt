package ru.rikmasters.gilty.core.viewmodel.trait

import androidx.compose.runtime.Composable
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.connector.TraitWrapper

/**
 * Базовый интерфейс для всех признаков
 */
sealed interface Trait { }

sealed class TraitWrapperFactory {
    
    fun <VM: ViewModel> createWrapper(): TraitWrapper<VM> =
        @Composable { vm, content -> Wrapper(vm, content) }
    
    @Composable
    abstract fun <VM: ViewModel> Wrapper(vm: VM, content: @Composable (VM) -> Unit)
}