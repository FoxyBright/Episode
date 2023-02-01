package ru.rikmasters.gilty.mainscreen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen.MainScreen
import ru.rikmasters.gilty.mainscreen.presentation.ui.reaction.ReactionScreen
import ru.rikmasters.gilty.mainscreen.viewmodels.FiltersViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.CalendarBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.MeetBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.TimeBsViewModel
import ru.rikmasters.gilty.shared.model.meeting.DemoCategoryModel

object Main: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        
        nested("main", "meetings") {
            
            screen<MainViewModel>("meetings") { vm, _ ->
                MainScreen(vm)
            }
            
            screen(
                "reaction?avatar={avatar}&response={response}",
                listOf(navArgument("avatar") {
                    type = NavType.StringType
                    defaultValue = ""
                }, navArgument("response") {
                    type = NavType.BoolType
                    defaultValue = true
                })
            ) {
                it.arguments?.getString("avatar")?.let { avatar ->
                    it.arguments?.getInt("meetType")?.let { response ->
                        ReactionScreen(avatar, DemoCategoryModel)
                    }
                }
            }
        }
    }
    
    
    override fun Module.koin() {
        singleOf(::MeetingManager)
        
        scope<MainViewModel> {
            scopedOf(::MainViewModel)
            scopedOf(::CalendarBsViewModel)
            scopedOf(::MeetBsViewModel)
            scopedOf(::TimeBsViewModel)
            scopedOf(::FiltersViewModel)
        }
    }
}