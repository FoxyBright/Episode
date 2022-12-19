package ru.rikmasters.gilty.addmeet

import org.koin.core.module.Module
import ru.rikmasters.gilty.addmeet.presentation.ui.category.CategoriesScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.complete.CompleteScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.ConditionsScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.DetailedScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.RequirementsScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.tags.TagsScreen
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object AddMeet : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("addmeet", "category") {
            screen("category") { CategoriesScreen() }
            screen("conditions") { ConditionsScreen() }
            screen("detailed") { DetailedScreen() }
            screen("tags") { TagsScreen() }
            screen("requirements") { RequirementsScreen() }
            screen("complete") { CompleteScreen() }
        }
    }

    override fun Module.koin() {}
}