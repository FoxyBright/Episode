package ru.rikmasters.gilty.notifications

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.bottomsheet.BottomSheet
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.notification.NotificationsData
import ru.rikmasters.gilty.notifications.presentation.ui.notification.NotificationsScreen
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.profile.ProfileData

object Notifications: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        nested("notification", "list") {
            screen<NotificationViewModel>("list") { vm, _ ->
                NotificationsScreen(vm)
            }
        }
    }
    
    override fun Module.koin() {
        singleOf(::NotificationViewModel)
    }
    
    override fun include() = setOf(
        NotificationsData, ProfileData,
        MeetingsData, BottomSheet
    )
}