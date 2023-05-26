package ru.rikmasters.gilty.core.navigation

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import ru.rikmasters.gilty.core.log.Loggable
import ru.rikmasters.gilty.core.util.extension.slash

@Stable
class NavState(
    
    internal val navHostController: NavHostController,
    
    val startDestination: String,
    
    ): Loggable {
    
    internal val routeOptions: MutableMap<String, NavOptions> = HashMap()
    
    private val navController = navHostController
    
    /**
     * Перейти по относительному пути (внутри текущего nested)
     *
     * @param dest название точки назначения
     *
     * @see [DeepNavGraphBuilder.nested]
     */
    fun navigate(dest: String) {
        val path = try {
            navHostController.currentDestination!!.route!!.run {
                if(contains('/'))
                    substringBeforeLast('/')
                else ""
            }
        } catch(e: RuntimeException) {
            throw IllegalStateException("Не удалось получить получить текущую позицию", e)
        }
        
        navigateAbsolute(path slash dest)
    }
    
    /**
     * Перейти по абсолютному пути
     *
     * @param dest полный путь
     */
    fun navigateAbsolute(dest: String) {
        // TODO Fast clicks
        navHostController.navigate(dest, routeOptions[dest])
    }
    
    /**
     * Очистка стека
     *
     * @param route маршрут пункта назначения
     */
    fun clearStackNavigation(route: String) {
        navController.navigate(route) {
            popUpTo(0)
        }
    }
    fun clearStackNavigationAbsolute(route:String){
        navHostController.navigate(route) {
            popUpTo(0)
        }
    }
    
    /**
     * путь назад по графу
     */
    fun navigationBack() {
        navController.popBackStack()
    }
}