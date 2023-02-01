package ru.rikmasters.gilty.auth.meetings

import ru.rikmasters.gilty.auth.meetings.MeetingWebSource.MeetFilterGroup
import ru.rikmasters.gilty.auth.meetings.MeetingWebSource.MeetFilterGroup.TODAY
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

data class MeetFilters(
    val group: MeetFilterGroup = TODAY,
    val categories: List<CategoryModel>? = null,
    val tags: List<TagModel>? = null,
    val radius: Int? = null,
    val lat: Int? = null,
    val lng: Int? = null,
    val meetTypes: List<MeetType>? = null,
    val onlyOnline: Boolean? = null,
    val genders: List<GenderType>? = null,
    val conditions: List<ConditionType>? = null,
    val dates: List<String>? = null,
    val time: String? = null
)