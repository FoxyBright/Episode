package ru.rikmasters.gilty.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavOptionsBuilder
import org.koin.androidx.compose.getKoin
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import kotlin.reflect.KClass

internal sealed interface Destination

internal interface Screen: Destination {
    val deepRoute: String
    val arguments: List<NamedNavArgument>
    val deepLinks: List<NavDeepLink>
    val navOptions: NavOptionsBuilder.() -> Unit
    val content: @Composable (NavBackStackEntry) -> Unit
}

internal data class SimpleScreen(
    override val deepRoute: String,
    override val arguments: List<NamedNavArgument>,
    override val deepLinks: List<NavDeepLink>,
    override val navOptions: NavOptionsBuilder.() -> Unit,
    override val content: @Composable (NavBackStackEntry) -> Unit
): Screen

internal data class VmScreen<T: ViewModel>(
    override val deepRoute: String,
    override val arguments: List<NamedNavArgument>,
    override val deepLinks: List<NavDeepLink>,
    override val navOptions: NavOptionsBuilder.() -> Unit,
    val vmClass: KClass<T>,
    val vmContent: @Composable (T, NavBackStackEntry) -> Unit
): Screen {
    override val content: @Composable (NavBackStackEntry) -> Unit = {
        vmContent(getKoin().get(vmClass), it)
    }
}

internal data class Dialog(
    val deepRoute: String,
    val arguments: List<NamedNavArgument>,
    val deepLinks: List<NavDeepLink>,
    val dialogProperties: DialogProperties,
    val navOptions: NavOptionsBuilder.() -> Unit,
    val content: @Composable (NavBackStackEntry) -> Unit
): Destination

internal data class Nested(
    val deepRoute: String,
    val startDestination: String,
    val builder: DeepNavGraphBuilder.() -> Unit
): Destination