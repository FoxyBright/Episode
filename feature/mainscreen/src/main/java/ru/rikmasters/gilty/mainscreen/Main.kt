package ru.rikmasters.gilty.mainscreen

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.MainScreen

object Main : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        screen("main"){ MainScreen() }
    }

    //TODO навигация с агрументами
//    nested("main", "meets") {
//        screen("meets?grid={grid}", listOf(navArgument("grid") {
//            type = NavType.BoolType
//            defaultValue = false
//        })) {
//            val grid = it.arguments!!.getBoolean("grid")
//            MainScreen(grid)
//        }
//    }

    override fun Module.koin() {}
}