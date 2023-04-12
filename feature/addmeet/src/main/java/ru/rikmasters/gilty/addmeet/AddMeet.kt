package ru.rikmasters.gilty.addmeet

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.addmeet.presentation.ui.category.CategoriesScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.complete.CompleteScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.ConditionsScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.DetailedScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.RequirementsScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.tags.TagsScreen
import ru.rikmasters.gilty.addmeet.viewmodel.*
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.*
import ru.rikmasters.gilty.bottomsheet.viewmodel.MapBsViewModel
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.profile.ProfileData

object AddMeet: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        nested("addmeet", "category") {
            
            screen<CategoryViewModel>("category") { vm, _ ->
                CategoriesScreen(vm)
            }
            
            screen<ConditionViewModel>("conditions") { vm, _ ->
                ConditionsScreen(vm)
            }
            
            screen<DetailedViewModel>("detailed") { vm, _ ->
                DetailedScreen(vm)
            }
            
            screen<TagsViewModel>("tags") { vm, _ ->
                TagsScreen(vm)
            }
            
            screen<RequirementsViewModel>("requirements") { vm, _ ->
                RequirementsScreen(vm)
            }
            
            screen<CompleteViewModel>("complete") { vm, _ ->
                CompleteScreen(vm)
            }
        }
    }
    
    override fun Module.koin() {
        
        singleOf(::CategoryViewModel)
        singleOf(::ConditionViewModel)
    
        singleOf(::DetailedViewModel)
        factoryOf(::DurationBsViewModel)
        factoryOf(::MapBsViewModel)
        factoryOf(::TimeBsViewModel)
        
        factoryOf(::TagsViewModel)
    
        singleOf(::RequirementsViewModel)
        factoryOf(::GenderBsViewModel)
        factoryOf(::OrientationBsViewModel)
        factoryOf(::AgeBsViewModel)

        singleOf(::CompleteViewModel)
    }
    
    override fun include() = setOf(MeetingsData, ProfileData)
}