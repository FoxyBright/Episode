package ru.rikmasters.gilty.core.viewmodel.connector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.Trait
import kotlin.reflect.KClass

@Composable
fun <T: ViewModel> rememberConnectorState(vm: T) = remember { ConnectorState(vm) }

@Immutable
class ConnectorState<T: ViewModel>(
    val vm: T,
) {
//    internal val traits = Trait::class.sealedSubclasses.associateWith { it.isInstance(vm) }
//
//    internal inline fun <reified T: Trait> isEnabled() = isEnabled(T::class)
//    internal fun <T: Trait> isEnabled(trait: KClass<T>) = traits[trait] ?: false
}