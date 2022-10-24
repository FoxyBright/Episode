package ru.rikmasters.gilty.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navOptions
import androidx.navigation.navigation
import ru.rikmasters.gilty.core.util.extension.slash


/**
 * Обёртка [NavGraphBuilder]
 */
class DeepNavGraphBuilder internal constructor(
    private val state: NavState,
    private val baseRoute: String? = null
) {
    private val destinations = mutableListOf<Destination>()

    /**
     * Реальный route будет сгенерирован по принципу $nested/$nested/.../$route
     *
     * @param navOptions параметры по умолчанию
     *
     * @see [NavGraphBuilder.composable]
     */
    fun screen(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = emptyList(),
        navOptions: NavOptionsBuilder.() -> Unit = { },
        content: @Composable (NavBackStackEntry) -> Unit
    ) {
        destinations += Screen(route.deep(), arguments, deepLinks, navOptions, content)
    }

    /**
     * Реальный route будет сгенерирован по принципу $nested/$nested/.../$route
     *
     * @param navOptions параметры по умолчанию
     *
     * @see [NavGraphBuilder.dialog]
     */
    fun dialogScreen(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = emptyList(),
        dialogProperties: DialogProperties = DialogProperties(),
        navOptions: NavOptionsBuilder.() -> Unit,
        content: @Composable (NavBackStackEntry) -> Unit
    ) {
        destinations += Dialog(route.deep(), arguments, deepLinks, dialogProperties, navOptions, content)
    }

    /**
     * Реальный route будет сгенерирован по принципу $nested/$nested/.../$route
     *
     * @see [NavGraphBuilder.navigation]
     */
    fun nested(
        route: String,
        startDestination: String,
        builder: DeepNavGraphBuilder.() -> Unit
    ) {
        destinations += Nested(route.deep(), startDestination, builder)
    }

    internal fun build(builder: NavGraphBuilder): Unit = builder.run {
        destinations.forEach {
            when(it) {
                is Screen -> {
                    processNavOptions(it.deepRoute, it.navOptions)
                    composable(
                        it.deepRoute,
                        it.arguments,
                        it.deepLinks,
                        it.content
                    )
                }

                is Dialog -> {
                    processNavOptions(it.deepRoute, it.navOptions)
                    dialog(
                        it.deepRoute,
                        it.arguments,
                        it.deepLinks,
                        it.dialogProperties,
                        it.content
                    )
                }

                is Nested ->
                    navigation(it.startDestination, it.deepRoute) {
                        DeepNavGraphBuilder(state, it.deepRoute)
                            .apply(it.builder).build(this)
                    }
            }
        }
    }

    private fun processNavOptions(route: String, builder: NavOptionsBuilder.() -> Unit) {
        state.routeOptions[route] = navOptions(builder)
    }

    private fun String.deep() =
        if(this.contains('/'))
            throw IllegalArgumentException("Путь не должен содержать '/' в названиях")
        else
            baseRoute slash this
}