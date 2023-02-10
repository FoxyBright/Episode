package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.meeting.LocationModel

data class Location(
    val hide: Boolean? = null,
    val lat: Int? = null,
    val lng: Int? = null,
    val country: String? = null,
    val subject: String? = null,
    val city: String? = null,
    val place: String? = null,
    val address: String? = null,
) {
    
    fun map() = LocationModel(
        hide, lat, lng,
        country.toString(),
        subject.toString(),
        city.toString(),
        place, address
    )
}