package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.GenderType.MALE
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

data class RequirementModel(
    
    val gender: String,
    
    val ageMin: Int,
    
    val ageMax: Int,
    
    val orientation: OrientationModel?,
)

val DemoRequirementModel = RequirementModel(
    MALE.name, (18), (40), OrientationModel(
        "HETERO", "Гетеросексуал(ка)"
    )
)
