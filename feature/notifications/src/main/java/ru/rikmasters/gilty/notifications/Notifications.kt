package ru.rikmasters.gilty.notifications

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

object Notifications : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("notifications", "leaveemotions") {
            screen("leaveemotions") {}
            screen("photosviewing") {}
        }
    }

    override fun Module.koin() {}
}