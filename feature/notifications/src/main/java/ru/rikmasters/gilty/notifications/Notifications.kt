package ru.rikmasters.gilty.notifications

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Notifications : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("notifications", "list") {
            screen("list") {}
            screen("emoji") {}
            screen("photos") {}
        }
    }

    override fun Module.koin() {}
}