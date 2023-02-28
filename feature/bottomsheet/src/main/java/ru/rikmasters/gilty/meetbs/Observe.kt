package ru.rikmasters.gilty.meetbs

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetbs.viewmodel.BsViewModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.*
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.profile.ProfileData

object Observe: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
    }
    
    override fun Module.koin() {
        singleOf(::BsViewModel)
        singleOf(::MeetingViewModel)
        singleOf(::ParticipantsViewModel)
        singleOf(::OrganizerViewModel)
        singleOf(::ReportsViewModel)
    }
    
    override fun include() = setOf(ProfileData, MeetingsData)
}