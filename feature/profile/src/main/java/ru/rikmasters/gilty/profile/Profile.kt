package ru.rikmasters.gilty.profile

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Profile : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        screen("userprofile") {}
    }

    override fun Module.koin() {}
}