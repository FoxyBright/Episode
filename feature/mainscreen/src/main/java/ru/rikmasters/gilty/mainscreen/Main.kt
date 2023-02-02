package ru.rikmasters.gilty.mainscreen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.auth.manager.ProfileManager
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen.MainScreen
import ru.rikmasters.gilty.mainscreen.presentation.ui.reaction.ReactionScreen
import ru.rikmasters.gilty.mainscreen.viewmodels.FiltersViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.RespondsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.CalendarBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.MeetBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.TimeBsViewModel

object Main: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        nested("main", "meetings") {
            
            screen<MainViewModel>("meetings") { vm, _ ->
                MainScreen(vm)
            }
            
            screen<RespondsViewModel>(
                "reaction?meetId={meetId}",
                listOf(navArgument("meetId") {
                    type = NavType.StringType
                    defaultValue = ""
                })
            ) { vm, it ->
                it.arguments?.getString("meetId")?.let { meetId ->
                    ReactionScreen(vm, meetId)
                }
            }
        }
    }
    
    override fun Module.koin() {
        
        singleOf(::MeetingManager)
        singleOf(::ProfileManager)
        
        scope<RespondsViewModel> {
            scopedOf(::RespondsViewModel)
        }
        
        scope<MainViewModel> {
            scopedOf(::MainViewModel)
            scopedOf(::CalendarBsViewModel)
            scopedOf(::MeetBsViewModel)
            scopedOf(::TimeBsViewModel)
            scopedOf(::FiltersViewModel)
        }
    }
}