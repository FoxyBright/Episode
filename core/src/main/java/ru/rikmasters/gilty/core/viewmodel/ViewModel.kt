package ru.rikmasters.gilty.core.viewmodel

import android.content.Context
import androidx.annotation.StringRes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import org.koin.core.component.createScope
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.core.common.ScopeComponent
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait

abstract class ViewModel:
    CoroutineController(),
    ScopeComponent,
    ScopeCallback,
    LoadingTrait {
    
    override var scope: Scope = createScope()
    
    private val _events = MutableSharedFlow<Event>()
    internal val events = _events.asSharedFlow()
    
    @Suppress("unused")
    protected suspend fun event(key: String) = event(key to null)
    private suspend fun event(pair: Pair<String, Any?>) =
        event(Event(pair))
    
    private suspend fun event(data: Event) = _events.emit(data)
    
    protected suspend fun makeToast(text: String) = event("toast" to text)
    
    @Suppress("unused")
    protected suspend fun makeToast(@StringRes text: Int) =
        makeToast(getKoin().get<Context>().getString(text))
    
    private val loadingMut = MutableStateFlow(false)
    override val loading = loadingMut.asStateFlow()
    
    private val _loadingPull = MutableStateFlow(false)
    override val pagingPull = _loadingPull.asStateFlow()
    
    protected suspend fun startPullLoading() = _loadingPull.emit(true)
    protected suspend fun stopPullLoading() = _loadingPull.emit(false)
    
    protected suspend fun startLoading() = loadingMut.emit(true)
    protected suspend fun stopLoading() = loadingMut.emit(false)
    protected suspend inline fun <T> loading(block: () -> T): T = try {
        startLoading()
        startPullLoading()
        val res = block()
        stopLoading()
        stopPullLoading()
        res
    } catch(e: Exception) {
        if(e is CancellationException)
            stopLoading()
        throw e
    }
    
    protected suspend inline fun <T> singleLoading(
        strategy: Strategy = DEFAULT_SINGLE_STRATEGY,
        noinline block: suspend CoroutineScope.() -> T,
    ): T = loading { single(strategy, block) }
    
    protected fun <T> Flow<T>.state(
        initial: T,
        started: SharingStarted = SharingStarted.Lazily,
    ): StateFlow<T> = stateIn(coroutineScope, started, initial)
    
    override fun onScopeClose(scope: Scope) {
        /* Для очистки */
    }
}