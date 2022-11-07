package ru.rikmasters.gilty.complaints

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Complaints : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        screen("complaints") {}
    }

    override fun Module.koin() {}
}