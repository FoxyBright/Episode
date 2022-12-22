package ru.rikmasters.gilty.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.composable
import ru.rikmasters.gilty.core.util.extension.slash
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import kotlin.collections.set
import kotlin.reflect.KClass


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
        destinations += SimpleScreen(route.deep(), arguments, deepLinks, navOptions, content)
    }
    
    inline fun <reified T: ViewModel> screen(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = emptyList(),
        noinline navOptions: NavOptionsBuilder.() -> Unit = { },
        noinline content: @Composable (T, NavBackStackEntry) -> Unit
    ) {
        screen(route, arguments, deepLinks, navOptions, T::class, content)
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <T: ViewModel> screen(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = emptyList(),
        navOptions: NavOptionsBuilder.() -> Unit = { },
        vmClass: KClass<T>,
        content: @Composable (T, NavBackStackEntry) -> Unit
    ) {
        destinations += VmScreen(route.deep(), arguments, deepLinks, navOptions, vmClass, content)
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
                        it.resolveNavArgs(),
                        it.deepLinks,
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
    
    private fun Screen.resolveNavArgs() =
        resolveNavArgs(deepRoute, navArgs)
    
    private fun resolveNavArgs(
        route: String,
        args: List<NamedNavArgument>
    ): List<NamedNavArgument> {
        var result: MutableList<NamedNavArgument>? = null
        val argsStr = route.substringAfter('?', "")
        Regex("(\\w+)=\\{(\\w+)\\}").findAll(argsStr).forEach { res ->
            val name = res.groups[2]?.value ?: return@forEach
            if(args.any { it.name == name }) return@forEach
            if(result == null) result = args.toMutableList()
            result!! += navArgument(name) {
                type = NavType.StringType
                defaultValue = ""
            }
        }
        return result ?: args
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