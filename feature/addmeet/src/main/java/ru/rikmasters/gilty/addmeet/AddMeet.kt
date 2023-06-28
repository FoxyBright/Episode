package ru.rikmasters.gilty.addmeet

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.addmeet.presentation.ui.category.CategoriesScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.complete.CompleteScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.ConditionsScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.DetailedScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.RequirementsScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.subcategory.SubcategoriesScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.tags.TagsScreen
import ru.rikmasters.gilty.addmeet.viewmodel.*
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.*
import ru.rikmasters.gilty.bottomsheet.viewmodel.MapBsViewModel
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.profile.ProfileData

object AddMeet : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        nested("addmeet", "category") {

            screen<CategoryViewModel>("category") { vm, _ ->
                CategoriesScreen(vm)
            }

            screen<SubcategoryViewModel>("subcategory") { vm, _ ->
                SubcategoriesScreen(vm)
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

        // category screen
        singleOf(::SubcategoryViewModel)
        singleOf(::ConditionViewModel)
        singleOf(::CategoryViewModel)

        // detailed screen
        factoryOf(::DurationBsViewModel)
        singleOf(::DetailedViewModel)
        factoryOf(::TimeBsViewModel)
        factoryOf(::MapBsViewModel)

        // tags screen
        factoryOf(::TagsViewModel)

        // requirements screen
        factoryOf(::OrientationBsViewModel)
        singleOf(::RequirementsViewModel)
        factoryOf(::GenderBsViewModel)
        factoryOf(::AgeBsViewModel)

        // complete screen
        singleOf(::CompleteViewModel)
    }

    override fun include() = setOf(
        MeetingsData, ProfileData
    )
}