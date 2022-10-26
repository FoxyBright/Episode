package ru.rikmasters.gilty.mainscreen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object MainScreen : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("main", "meets") {
            screen("meets?grid={grid}", listOf(navArgument("grid") {
                type = NavType.BoolType
                defaultValue = false
            })) {
                val grid = it.arguments!!.getBoolean("grid")
                log.v("Grid $grid")
            }
        }
    }

    //TODO вызов такой:
    //  val nav = get<NavState>()
    //  Button({nav.navigate("main/meets")}) {
    //  Text("To Meets", color = MaterialTheme.colorScheme.background)
    //  }

    override fun Module.koin() {}
}