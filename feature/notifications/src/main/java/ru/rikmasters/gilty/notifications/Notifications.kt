package ru.rikmasters.gilty.notifications

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.notifications.presentation.ui.notification.NotificationsScreen

object Notifications : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("notification", "list") {
            screen("list") { NotificationsScreen() }
        }
    }

    override fun Module.koin() {}
}