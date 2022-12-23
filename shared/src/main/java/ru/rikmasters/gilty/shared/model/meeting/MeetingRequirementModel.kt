package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.SexType
import ru.rikmasters.gilty.shared.model.enumeration.SexType.FEMALE
import ru.rikmasters.gilty.shared.model.profile.DemoOrientationModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

data class MeetingRequirementModel(

    val number: Int,

    val gender: SexType,

    val orientation: OrientationModel

)

val DemoMeetingRequirementModel = MeetingRequirementModel(

    number = 5,

    gender = FEMALE,

    orientation = DemoOrientationModel
)

val ListDemoMeetingRequirementModel =
    listOf(DemoMeetingRequirementModel, DemoMeetingRequirementModel)

