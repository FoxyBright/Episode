package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.meeting.LocationModel

data class Location(
    val hide: Boolean? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val place: String? = null,
    val address: String? = null,
) {
    
    fun map() = LocationModel(
        hide, lat, lng, place, address
    )
}