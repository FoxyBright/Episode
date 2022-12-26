package ru.rikmasters.gilty.core.viewmodel.connector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import kotlin.reflect.KClass

@Composable
inline fun <reified T: ViewModel> localConnectorOf(): ConnectorState<T> =
    localConnectorOf(T::class)

@Composable
internal inline fun <reified T: ViewModel> localConnectorProviderOf() =
    localConnectorProviderOf(T::class)



private val providers by lazy {
    HashMap<KClass<*>, ProvidableCompositionLocal<ConnectorState<*>?>>()
}

@Composable
fun <T: ViewModel> localConnectorOf(clazz: KClass<T>): ConnectorState<T> =
    localConnectorProviderOf(clazz).current
        ?: throw IllegalStateException("ConnectorState не найден")

@Suppress("UNCHECKED_CAST")
internal fun <T: ViewModel> localConnectorProviderOf(
    clazz: KClass<T>
): ProvidableCompositionLocal<ConnectorState<T>?> {
    
    val provider = providers[clazz]
        ?: (compositionLocalOf<ConnectorState<T>?> { null }
                as ProvidableCompositionLocal<ConnectorState<*>?>)
            .also { providers[clazz] = it }
    
    return provider as ProvidableCompositionLocal<ConnectorState<T>?>
}