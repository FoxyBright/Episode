package ru.rikmasters.gilty.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import ru.rikmasters.gilty.core.log.log

class DeepNavGraphBuilder internal constructor(
    private val baseRoute: String? = null
) {
    private val destinations = mutableListOf<Destination>()

    companion object {
    }

    fun screen(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = emptyList(),
        content: @Composable (NavBackStackEntry) -> Unit
    ) {
        destinations += Screen(route.deep(), arguments, deepLinks, content)
    }

    fun dialogScreen(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = emptyList(),
        dialogProperties: DialogProperties = DialogProperties(),
        content: @Composable (NavBackStackEntry) -> Unit
    ) {
        destinations += Dialog(route.deep(), arguments, deepLinks, dialogProperties, content)
    }

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
                is Screen ->
                    composable(
                        it.deepRoute,
                        it.arguments,
                        it.deepLinks,
                        it.content
                    )

                is Dialog ->
                    dialog(
                        it.deepRoute,
                        it.arguments,
                        it.deepLinks,
                        it.dialogProperties,
                        it.content
                    )

                is Nested ->
                    navigation(it.startDestination, it.deepRoute) {
                        DeepNavGraphBuilder(it.deepRoute).apply(it.builder).build(this)
                    }
            }
        }
    }
    private fun String.deep() = baseRoute slash this


    private infix fun String?.slash(next: String) =
        if(this == null)
            next
        else
            (this + if(this.endsWith('/')) next else "/$next")
}