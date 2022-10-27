package ru.rikmasters.gilty.mainscreen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.MainScreen
import ru.rikmasters.gilty.mainscreen.presentation.ui.reaction.ReactionScreen

object Main : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {

        nested("main", "meetings") {
            screen("meetings") { MainScreen() }
            screen("reaction?avatar={avatar}", listOf(navArgument("avatar") {
                type = NavType.StringType
                defaultValue = ""
            })) {
                it.arguments?.getString("avatar")
                    ?.let { avatar -> ReactionScreen(avatar) }
            }
        }
    }

    override fun Module.koin() {}
}