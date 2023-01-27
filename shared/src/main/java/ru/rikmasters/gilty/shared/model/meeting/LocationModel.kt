package ru.rikmasters.gilty.shared.model.meeting

data class LocationModel(
    val hide: Boolean?,
    val lat: Int?,
    val lng: Int?,
    val place: String?,
    val address: String?,
)

@Suppress("unused")
val DemoLocationModel = LocationModel(
    hide = true,
    lat = 0,
    lng = 0,
    place = "place",
    address = "address"
)