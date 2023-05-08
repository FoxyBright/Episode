package ru.rikmasters.gilty.profile.models

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.models.meets.Category

data class ProfileCategories(
    val list: List<Category>,
): DomainEntity