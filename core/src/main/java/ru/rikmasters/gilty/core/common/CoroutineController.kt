package ru.rikmasters.gilty.core.common

import kotlinx.coroutines.*
import ru.rikmasters.gilty.core.util.inline.methodName
import ru.rikmasters.gilty.core.viewmodel.Strategy

abstract class CoroutineController {
    
    companion object {
        val DEFAULT_SINGLE_STRATEGY = Strategy.CANCEL
    }
    
    protected val scope = CoroutineScope(SupervisorJob())
    
    private val calls: MutableMap<String, Deferred<*>> = HashMap()
    
    @Suppress("UNCHECKED_CAST")
    protected suspend inline fun <T> single(
        strategy: Strategy = DEFAULT_SINGLE_STRATEGY,
        noinline block: suspend CoroutineScope.() -> T
    ): T = single(methodName(), strategy, block)
    
    protected suspend fun <T> single(
        key: String,
        strategy: Strategy = DEFAULT_SINGLE_STRATEGY,
        block: suspend CoroutineScope.() -> T
    ): T {
        calls[key]?.let {
            when(strategy) {
                Strategy.CANCEL -> it.cancel("Отмена одиночной задачи $key")
                Strategy.THROW -> throw IllegalStateException("Одиночная задача $key уже начата")
                Strategy.JOIN -> return it.await() as T
            }
        }
    
        val deferred = scope.async { block() }
        calls[key] = deferred
        val res = deferred.await()
        calls.remove(key, deferred)
        return res
    }
}