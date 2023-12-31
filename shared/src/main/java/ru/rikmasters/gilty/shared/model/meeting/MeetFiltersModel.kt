package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.*
import ru.rikmasters.gilty.shared.model.enumeration.MeetFilterGroupType.TODAY

data class MeetFiltersModel(
    val group: MeetFilterGroupType = TODAY,
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
) {
    
    fun isNotNullOrEmpty() = (!categories.isNullOrEmpty()
            || !tags.isNullOrEmpty()
            || radius != null
            || !meetTypes.isNullOrEmpty()
            || onlyOnline != null
            || !conditions.isNullOrEmpty()
            || !dates.isNullOrEmpty()
            || !time.isNullOrBlank()
            || city != null)
}