package ru.rikmasters.gilty.core.viewmodel.connector

import androidx.compose.runtime.*
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.getScopeId
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatformTools
import ru.rikmasters.gilty.core.common.Component
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import kotlin.reflect.KClass

@OptIn(KoinInternalApi::class)
val LocalScope = compositionLocalOf {
    KoinPlatformTools.defaultContext().get().scopeRegistry.rootScope
}

@Composable
inline fun <reified T: ViewModel> rememberConnectorState(scope: Scope? = null): ConnectorState<T> =
    rememberConnectorState(T::class, scope)

@Composable
fun <T: ViewModel> rememberConnectorState(clazz: KClass<T>, scope: Scope? = null): ConnectorState<T> {
    
    var currentScope = (scope ?: LocalScope.current)
    
    val vm = currentScope.getOrNull<T>(clazz)
        ?: clazz.getScopeId().let { scopeId ->
            currentScope = currentScope.getKoin().let {
                it.getScopeOrNull(scopeId)
                    ?: it.createScope(scopeId, TypeQualifier(clazz))
            }
            currentScope.get(clazz)
        }
    
    vm.scope = currentScope
    vm.scope.registerCallback(vm)
    
    return rememberConnectorState(vm)
}

@Composable
fun <T: ViewModel> rememberConnectorState(vm: T): ConnectorState<T> =
    remember { ConnectorState(vm) }

@Immutable
class ConnectorState<T: ViewModel>(
    val vm: T
): Component {
    
    val clazz: KClass<out T> = vm::class
}