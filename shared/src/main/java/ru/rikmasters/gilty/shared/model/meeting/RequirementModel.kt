package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.MALE
import ru.rikmasters.gilty.shared.model.profile.DemoOrientationModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

data class RequirementModel(
    
    val gender: GenderType?,
    
    val ageMin: Int?,
    
    val ageMax: Int?,
    
    val orientation: OrientationModel?,
)

val DemoRequirementModel = RequirementModel(
    gender = MALE,
    ageMin = 18,
    ageMax = 40,
    orientation = DemoOrientationModel
)
