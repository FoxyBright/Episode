package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.profile.OrientationModel

data class Orientation(
    val id: String,
    val name: String,
) {
    
    fun map() = OrientationModel(id, name)
}