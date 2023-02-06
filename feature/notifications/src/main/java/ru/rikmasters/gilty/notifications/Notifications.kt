package ru.rikmasters.gilty.notifications

import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.notifications.presentation.ui.notification.NotificationsScreen
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.notifications.viewmodel.RespondsViewModel

object Notifications: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        nested("notification", "list") {
            
            screen<NotificationViewModel>("list") { vm, _ ->
                NotificationsScreen(vm)
            }
            
        }
    }
    
    
    override fun Module.koin() {
        scope<NotificationViewModel> {
            scopedOf(::NotificationViewModel)
            scopedOf(::RespondsViewModel)
        }
    }
}