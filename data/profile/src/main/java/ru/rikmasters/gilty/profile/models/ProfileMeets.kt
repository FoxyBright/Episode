package ru.rikmasters.gilty.profile.models

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.models.meets.Meeting

data class ProfileMeets(
    val type: MeetingsType,
    val list: List<Meeting>,
): DomainEntity