package ru.rikmasters.gilty.shared.model

import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel.DEFAULT


data class LastRespond(
    val image: String?,
    val isOnline: Boolean,
    val group: UserGroupTypeModel,
    val count: Int,
) {
    
    constructor(): this(
        image = "",
        isOnline = false,
        group = DEFAULT,
        count = 0
    )
}
