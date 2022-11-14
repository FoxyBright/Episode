package ru.rikmasters.gilty.addmeet

import org.koin.core.module.Module
import ru.rikmasters.gilty.addmeet.presentation.ui.category.CategoriesScreen
import ru.rikmasters.gilty.addmeet.presentation.ui.conditions.ConditionsScreen
import ru.rikmasters.gilty.core.app.EntrypointResolver
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object AddMeet : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        screen("category") { CategoriesScreen() }
        nested("addmeet", "category") {
            screen("category") { CategoriesScreen() }
            screen("conditions") { ConditionsScreen() }
            screen("tags") { /*TODO теги и параметры*/ }
            screen("requirements") { /*TODO требования к участникам*/ }
        }
    }

    override fun Module.koin() {
        single { EntrypointResolver { "category" } }
    }
}