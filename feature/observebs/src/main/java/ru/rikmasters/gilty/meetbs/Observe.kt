package ru.rikmasters.gilty.meetbs

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetbs.viewmodel.ObserveViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.MeetingViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.ParticipantsViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.UserViewModel
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.profile.ProfileData

object Observe: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
    }
    
    override fun Module.koin() {
        singleOf(::ObserveViewModel)
        singleOf(::MeetingViewModel)
        singleOf(::ParticipantsViewModel)
        singleOf(::UserViewModel)
    }
    
    override fun include() = setOf(ProfileData, MeetingsData)
}