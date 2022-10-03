package ru.rikmasters.gilty.presentation.model.meeting

import ru.rikmasters.gilty.presentation.model.profile.GenderModel
import ru.rikmasters.gilty.presentation.model.profile.GenderModelDemo
import ru.rikmasters.gilty.presentation.model.profile.OrientationModel
import ru.rikmasters.gilty.presentation.model.profile.OrientationModelDemo

data class MeetingRequirementModel(

    val number: Int,

    val gender: GenderModel,

    val orientation: OrientationModel

)

val DemoMeetingRequirementModel = MeetingRequirementModel(

    number = 5,

    gender = GenderModelDemo,

    orientation = OrientationModelDemo
)

val ListDemoMeetingRequirementModel = listOf(DemoMeetingRequirementModel, DemoMeetingRequirementModel)

