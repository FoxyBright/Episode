package ru.rikmasters.gilty.core.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import ru.rikmasters.gilty.core.common.Component
import ru.rikmasters.gilty.core.common.CoroutineController

abstract class ViewModel: CoroutineController(), Component {
    
    
    private val loadingMut = MutableStateFlow(false)
    val loading = loadingMut.asStateFlow()
    
    protected suspend fun startLoading() = loadingMut.emit(true)
    protected suspend fun stopLoading() = loadingMut.emit(false)
    protected suspend inline fun <T> loading(block: () -> T): T {
        startLoading()
        val res = block()
        stopLoading()
        return res
    }
    
    
    protected suspend inline fun <T> singleLoading(
        strategy: Strategy = DEFAULT_SINGLE_STRATEGY,
        noinline block: suspend CoroutineScope.() -> T
    ): T = loading { single(strategy, block) }
    
    
    protected fun <T> Flow<T>.state(
        initial: T,
        started: SharingStarted = SharingStarted.Lazily
    ): StateFlow<T> = stateIn(scope, started, initial)
}