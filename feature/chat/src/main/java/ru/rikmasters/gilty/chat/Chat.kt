package ru.rikmasters.gilty.chat

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Chat : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        screen("chat") {}
    }

    override fun Module.koin() {}
}