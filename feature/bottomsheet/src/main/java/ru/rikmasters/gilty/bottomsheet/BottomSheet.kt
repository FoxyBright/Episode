package ru.rikmasters.gilty.bottomsheet

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.bottomsheet.viewmodel.*
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.data.reports.ReportsData
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.profile.ProfileData
import ru.rikmasters.gilty.yandexmap.YandexMapViewModel

object BottomSheet: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
    }
    
    override fun Module.koin() {
        singleOf(::ParticipantsBsViewModel)
        singleOf(::RespondsBsViewModel)
        singleOf(::YandexMapViewModel)
        singleOf(::UserBsViewModel)
        singleOf(::MeetingBsViewModel)
        singleOf(::ReportsBsViewModel)
    }
    
    override fun include() = setOf(
        ProfileData, MeetingsData, ReportsData,
    )
}