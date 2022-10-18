package ru.rikmasters.gilty.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink

internal sealed interface Destination

internal data class Screen(
    val deepRoute: String,
    val arguments: List<NamedNavArgument>,
    val deepLinks: List<NavDeepLink>,
    val content: @Composable (NavBackStackEntry) -> Unit
): Destination

internal data class Dialog(
    val deepRoute: String,
    val arguments: List<NamedNavArgument>,
    val deepLinks: List<NavDeepLink>,
    val dialogProperties: DialogProperties,
    val content: @Composable (NavBackStackEntry) -> Unit
): Destination

internal data class Nested(
    val deepRoute: String,
    val startDestination: String,
    val builder: DeepNavGraphBuilder.() -> Unit
): Destination