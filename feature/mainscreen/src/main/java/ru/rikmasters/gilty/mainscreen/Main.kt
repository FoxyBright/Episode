package ru.rikmasters.gilty.mainscreen

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.mainscreen.presentation.ui.MainScreen
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
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
        }
    }
    
    override fun Module.koin() {
        factoryOf(::CalendarBsViewModel)
        singleOf(::FiltersBsViewModel)
        factoryOf(::TimeBsViewModel)
        singleOf(::MainViewModel)
    }
    
    override fun include() = setOf(MeetingsData, ProfileData)
}