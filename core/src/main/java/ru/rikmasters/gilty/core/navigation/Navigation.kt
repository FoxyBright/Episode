package ru.rikmasters.gilty.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
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
    val internalEntrypoint = "internal_entrypoint"
    NavHost(
        state.navHostController,
        internalEntrypoint,
        modifier,
        route
    ) {
        deepNavGraphBuilder(state, builder)
        composable(internalEntrypoint) {
            LaunchedEffect(Unit) {
                state.navigateAbsolute(state.startDestination)
            }
        }
    }
}