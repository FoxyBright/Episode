package ru.rikmasters.gilty.mainscreen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen.MainScreen
import ru.rikmasters.gilty.mainscreen.presentation.ui.reaction.ReactionScreen
import ru.rikmasters.gilty.shared.model.meeting.DemoCategoryModel

object Main: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        
        nested("main", "meetings") {
            
            screen("meetings") { MainScreen() }
            
            screen(
                "reaction?avatar={avatar}&meetType={meetType}",
                listOf(navArgument("avatar") {
                    type = NavType.StringType
                    defaultValue = ""
                }, navArgument("meetType") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) {
                it.arguments?.getString("avatar")?.let { avatar ->
                    it.arguments?.getInt("meetType")?.let { meetType ->
                        ReactionScreen(avatar, DemoCategoryModel)
                    }
                }
            }
        }
    }
    
    override fun Module.koin() {}
}