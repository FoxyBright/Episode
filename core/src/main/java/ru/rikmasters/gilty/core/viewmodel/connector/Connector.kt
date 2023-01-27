package ru.rikmasters.gilty.core.viewmodel.connector

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.TraitWrapperFactory
import kotlin.reflect.KClass

@Composable
inline fun <reified T: ViewModel> Connector(
    scope: Scope,
    noinline content: @Composable (T) -> Unit
) { Connector(T::class, scope, content) }

@Composable
inline fun <reified T: ViewModel> Connector(
    noinline content: @Composable (T) -> Unit
) { Connector(T::class, null, content) }

@Composable
inline fun <reified T: ViewModel> Connector(
    vm: T,
    noinline content: @Composable (T) -> Unit
) { Connector(rememberConnectorState(vm), T::class, content) }

@Composable
fun <T: ViewModel> Connector(
    clazz: KClass<T>,
    scope: Scope? = null,
    content: @Composable (T) -> Unit
) { Connector(rememberConnectorState(clazz, scope), clazz, content) }


@Composable
fun <T: ViewModel> Connector(
    state: ConnectorState<T>,
    clazz: KClass<T>,
    content: @Composable (T) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(state.vm) {
        state.vm.events.collect {
            when(it.key) {
                "toast" -> Toast.makeText(context, it.data as String, Toast.LENGTH_SHORT).show()
            }
        }
    }
    DisposableEffect(state.vm.scope) {
        onDispose {
            state.vm.scope.scopeQualifier.let{
                if(it is TypeQualifier && it.type == state.vm::class)
                    state.vm.scope.close()
            }
        }
    }
    CompositionLocalProvider(
        localConnectorProviderOf(clazz) provides state,
        LocalScope provides state.vm.scope
    ) {
        content(state.vm)
    }
}

@Composable
@Suppress("unused")
inline fun <reified T: ViewModel> Use(
    vararg traits: TraitWrapperFactory,
    crossinline content: @Composable () -> Unit
) = Use(T::class, *traits) { content() }

@Composable
fun <T: ViewModel> Use(
    clazz: KClass<T>,
    vararg traits: TraitWrapperFactory,
    content: @Composable () -> Unit
) {
    if(LocalInspectionMode.current) {
        content()
        return
    }
    val state = localConnectorOf(clazz)
    Use(state.vm, *traits) { content() }
}

@Composable
fun <T: ViewModel> Use(
    vm: T,
    vararg traits: TraitWrapperFactory,
    content: @Composable (T) -> Unit
) {
    remember {
        traits
            .map { it.createWrapper<T>() }
            .fold(content) { acc, wrapper -> { wrapper(it, acc) } }
    }(vm)
}