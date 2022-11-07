package ru.rikmasters.gilty.addmeet

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object AddMeet : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        screen("add") { }
    }

    override fun Module.koin() {}
}