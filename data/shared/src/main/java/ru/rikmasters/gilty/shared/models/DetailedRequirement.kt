package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.meeting.RequirementModel

data class DetailedRequirement(
    val number: Int? = null,
    val gender: String? = null,
    val orientation: Orientation? = null,
    val age: Age? = null,
) {
    
    fun map() = RequirementModel(
        gender?.let { GenderType.valueOf(it) },
        age?.min,
        age?.max,
        orientation?.map()
    )
}

data class Age(
    val min: Int,
    val max: Int,
)