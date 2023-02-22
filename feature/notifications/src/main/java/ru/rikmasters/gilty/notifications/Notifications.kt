package ru.rikmasters.gilty.notifications

import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetbs.Observe
import ru.rikmasters.gilty.meetbs.viewmodel.ObserveViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.MeetingViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.ParticipantsViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.UserViewModel
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.notifications.presentation.ui.notification.NotificationsScreen
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.RespondsBsViewModel
import ru.rikmasters.gilty.profile.ProfileData
import ru.rikmasters.gilty.push.NotificationData

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
            scopedOf(::ObserveViewModel)
            singleOf(::MeetingViewModel)
            scopedOf(::ParticipantsViewModel)
            scopedOf(::UserViewModel)
        }
    }
    
    override fun include() = setOf(
        NotificationData, ProfileData, MeetingsData, Observe
    )
}