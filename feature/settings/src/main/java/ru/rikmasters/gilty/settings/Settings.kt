package ru.rikmasters.gilty.settings

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Settings : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        screen("settings") {}
    }

    override fun Module.koin() {}
}