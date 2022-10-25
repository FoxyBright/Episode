package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.profile.DemoGenderModel
import ru.rikmasters.gilty.shared.model.profile.DemoOrientationModel
import ru.rikmasters.gilty.shared.model.profile.GenderModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

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

val ListDemoMeetingRequirementModel =
    listOf(DemoMeetingRequirementModel, DemoMeetingRequirementModel)

