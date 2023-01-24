package ru.rikmasters.gilty.addmeet

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.addmeet.presentation.ui.category.CategoriesScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.complete.CompleteScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.ConditionsScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.DetailedScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.RequirementsScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.tags.TagsScreen
import ru.rikmasters.gilty.addmeet.viewmodel.*
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.auth.meetings.MeetingWebSource
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

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
        singleOf(::MeetingWebSource)
        singleOf(::MeetingManager)
        
        scope<CategoryViewModel> {
            singleOf(::CategoryViewModel)
        }
        
        scope<ConditionViewModel> {
            singleOf(::ConditionViewModel)
        }
        
        scope<DetailedViewModel> {
            singleOf(::DetailedViewModel)
        }
        
        scope<TagsViewModel> {
            singleOf(::TagsViewModel)
        }
        
        scope<RequirementsViewModel> {
            singleOf(::RequirementsViewModel)
        }
        
        scope<CompleteViewModel> {
            singleOf(::CompleteViewModel)
        }
    }
}