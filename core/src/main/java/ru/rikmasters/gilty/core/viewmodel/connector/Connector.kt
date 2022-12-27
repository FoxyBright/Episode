package ru.rikmasters.gilty.core.viewmodel.connector

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalInspectionMode
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.Trait
import ru.rikmasters.gilty.core.viewmodel.trait.TraitWrapperFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

@Composable
inline fun <reified T: ViewModel> Connector(
    vm: T,
    noinline content: @Composable (T) -> Unit
) { Connector(rememberConnectorState(vm), content) }

@Composable
inline fun <reified T: ViewModel> Connector(
    state: ConnectorState<T>,
    noinline content: @Composable (T) -> Unit
) { Connector(state, T::class, content) }

@Composable
fun <T: ViewModel> Connector(
    state: ConnectorState<T>,
    clazz: KClass<T>,
    content: @Composable (T) -> Unit
) {
    CompositionLocalProvider(
        localConnectorProviderOf(clazz) provides state
    ) {
        content(state.vm)
    }
}

@Composable
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