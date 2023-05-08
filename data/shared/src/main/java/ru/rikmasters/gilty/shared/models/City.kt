package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.meeting.CityModel

data class City(
    val id: Int,
    val name: String,
) {
    
    fun map() = CityModel(
        id, name
    )
}