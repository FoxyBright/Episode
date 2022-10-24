package ru.rikmasters.gilty.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.runBlocking

fun NavGraphBuilder.deepNavGraphBuilder(
    state: NavState,
    builder: DeepNavGraphBuilder.() -> Unit
) = DeepNavGraphBuilder(state).apply(builder).build(this)

@Composable
fun DeepNavHost(
    state: NavState,
    modifier: Modifier = Modifier,
    route: String? = null,
    builder: DeepNavGraphBuilder.() -> Unit
) {
    NavHost(
        state.navHostController,
        state.startDestination,
        modifier,
        route
    ) { deepNavGraphBuilder(state, builder) }
}