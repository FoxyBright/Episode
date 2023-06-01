package ru.rikmasters.gilty.meetings

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.meetings.addmeet.AddMeet
import ru.rikmasters.gilty.meetings.addmeet.AddMeetStorage

object MeetingsData: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity<AddMeet>()
    }
    
    override fun Module.koin() {
        singleOf(::MeetingRepository)
        singleOf(::MeetingWebSource)
        singleOf(::MeetingManager)
        singleOf(::AddMeetStorage)
    }
}