package ru.rikmasters.gilty.presentation.model.meeting

import ru.rikmasters.gilty.presentation.model.profile.GenderModel
import ru.rikmasters.gilty.presentation.model.profile.DemoGenderModel
import ru.rikmasters.gilty.presentation.model.profile.OrientationModel
import ru.rikmasters.gilty.presentation.model.profile.DemoOrientationModel

data class MeetingRequirementModel(

    val number: Int,

    val gender: GenderModel,

    val orientation: OrientationModel

)

val DemoMeetingRequirementModel = MeetingRequirementModel(

    number = 5,

    gender = DemoGenderModel,

    orientation = DemoOrientationModel
)

val ListDemoMeetingRequirementModel = listOf(DemoMeetingRequirementModel, DemoMeetingRequirementModel)

