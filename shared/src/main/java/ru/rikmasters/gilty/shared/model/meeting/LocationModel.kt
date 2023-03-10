package ru.rikmasters.gilty.shared.model.meeting

data class LocationModel(
    val hide: Boolean?,
    val lat: Double?,
    val lng: Double?,
    val place: String?,
    val address: String?,
)

@Suppress("unused")
val DemoLocationModel = LocationModel(
    hide = true,
    lat = 0.0,
    lng = 0.0,
    place = "place",
    address = "address"
)