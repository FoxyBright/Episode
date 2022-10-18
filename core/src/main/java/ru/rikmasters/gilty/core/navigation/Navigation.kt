package ru.rikmasters.gilty.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

fun NavGraphBuilder.deepNavGraphBuilder(
    builder: DeepNavGraphBuilder.() -> Unit
) = DeepNavGraphBuilder().apply(builder).build(this)

@Composable
fun DeepNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    route: String? = null,
    builder: DeepNavGraphBuilder.() -> Unit
) {
    NavHost(
        navController,
        startDestination,
        modifier,
        route
    ) { deepNavGraphBuilder(builder) }
}