package ru.rikmasters.gilty.core.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.rikmasters.gilty.core.common.Component
import ru.rikmasters.gilty.core.util.inline.methodName

abstract class ViewModel: Component {
    
    companion object {
        val DEFAULT_SINGLE_STRATEGY = Strategy.CANCEL
    }

    protected val scope = CoroutineScope(SupervisorJob())
    
    
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
    
    
    protected val calls: MutableMap<String, Deferred<*>> = HashMap()
    
    protected suspend inline fun <T> singleLoading(
        strategy: Strategy = DEFAULT_SINGLE_STRATEGY,
        crossinline block: suspend CoroutineScope.() -> T
    ): T = loading { single(strategy, block) }
    
    @Suppress("UNCHECKED_CAST")
    protected suspend inline fun <T> single(
        strategy: Strategy = DEFAULT_SINGLE_STRATEGY,
        crossinline block: suspend CoroutineScope.() -> T
    ): T {
        val methodName = methodName()
        calls[methodName]?.let {
            when(strategy) {
                Strategy.CANCEL -> it.cancel("Отмена одиночной задачи $methodName")
                Strategy.THROW -> throw IllegalStateException("Одиночная задача $methodName уже начата")
                Strategy.JOIN -> return it.await() as T
            }
        }
        
        val deferred = scope.async { block() }
        calls[methodName] = deferred
        val res = deferred.await()
        calls.remove(methodName, deferred)
        return res
    }
    
    
    protected fun <T> Flow<T>.state(
        initial: T,
        started: SharingStarted = SharingStarted.Lazily
    ): StateFlow<T> = stateIn(scope, started, initial)
}