package ru.rikmasters.gilty.shared.models.meets

import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.CityModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

data class MeetFiltersRequest(
    val group: String,
    val categories: List<CategoryModel>? = null,
    val tags: List<TagModel>? = null,
    val radius: Int? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val meetTypes: List<MeetType>? = null,
    val onlyOnline: Boolean? = null,
    val genders: List<GenderType>? = null,
    val conditions: List<ConditionType>? = null,
    val dates: List<String>? = null,
    val time: String? = null,
    val city: CityModel? = null,
)