package ru.rikmasters.gilty.notifications

import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.bottomsheet.BottomSheet
import ru.rikmasters.gilty.bottomsheet.viewmodel.components.MeetingViewModel
import ru.rikmasters.gilty.bottomsheet.viewmodel.components.OrganizerViewModel
import ru.rikmasters.gilty.bottomsheet.viewmodel.components.ParticipantsViewModel
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.notification.NotificationsData
import ru.rikmasters.gilty.notifications.presentation.ui.notification.NotificationsScreen
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.RespondsBsViewModel
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
        scope<NotificationViewModel> {
            scopedOf(::NotificationViewModel)
            scopedOf(::RespondsBsViewModel)
            singleOf(::MeetingViewModel)
            scopedOf(::ParticipantsViewModel)
            scopedOf(::OrganizerViewModel)
        }
    }
    
    override fun include() = setOf(
        NotificationsData, ProfileData,
        MeetingsData, BottomSheet
    )
}