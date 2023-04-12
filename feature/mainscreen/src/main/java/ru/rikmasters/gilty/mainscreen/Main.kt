package ru.rikmasters.gilty.mainscreen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.mainscreen.presentation.ui.MainScreen
import ru.rikmasters.gilty.mainscreen.presentation.ui.MeetRespondScreen
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.RespondsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.CalendarBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.FiltersBsViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.TimeBsViewModel
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.profile.ProfileData

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
                    MeetRespondScreen(vm, meetId)
                }
            }
        }
    }
    
    override fun Module.koin() {
        factoryOf(::CalendarBsViewModel)
        factoryOf(::FiltersBsViewModel)
        singleOf(::RespondsViewModel)
        factoryOf(::TimeBsViewModel)
        singleOf(::MainViewModel)
    }
    
    override fun include() = setOf(MeetingsData, ProfileData)
}