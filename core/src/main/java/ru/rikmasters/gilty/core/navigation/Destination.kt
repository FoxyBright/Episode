package ru.rikmasters.gilty.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import org.koin.androidx.compose.getKoin
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.connector.Connector
import ru.rikmasters.gilty.core.viewmodel.connector.rememberConnectorState
import kotlin.reflect.KClass

internal sealed interface Destination

internal interface Screen: Destination {
    val deepRoute: String
    val navArgs: List<NamedNavArgument>
    val deepLinks: List<NavDeepLink>
    val navOptions: NavOptionsBuilder.() -> Unit
    val content: @Composable (NavBackStackEntry) -> Unit
}

internal data class SimpleScreen(
    override val deepRoute: String,
    override val navArgs: List<NamedNavArgument>,
    override val deepLinks: List<NavDeepLink>,
    override val navOptions: NavOptionsBuilder.() -> Unit,
    override val content: @Composable (NavBackStackEntry) -> Unit
): Screen

internal data class VmScreen<T: ViewModel>(
    override val deepRoute: String,
    override val navArgs: List<NamedNavArgument>,
    override val deepLinks: List<NavDeepLink>,
    override val navOptions: NavOptionsBuilder.() -> Unit,
    val vmClass: KClass<T>,
    val vmContent: @Composable (T, NavBackStackEntry) -> Unit
): Screen {
    override val content: @Composable (NavBackStackEntry) -> Unit = {
        val state = rememberConnectorState(getKoin().get(vmClass) as T)
        Connector(state, vmClass) { vm ->
            vmContent(vm, it)
        }
    }
}

internal data class Nested(
    val deepRoute: String,
    val startDestination: String,
    val builder: DeepNavGraphBuilder.() -> Unit
): Destination