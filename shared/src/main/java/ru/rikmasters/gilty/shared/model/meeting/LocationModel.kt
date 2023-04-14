package ru.rikmasters.gilty.shared.model.meeting

data class LocationModel(
    val hide: Boolean? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val place: String? = null,
    val address: String? = null,
    val country: String? = null,
)

@Suppress("unused")
val DemoLocationModel = LocationModel(
    hide = true,
    lat = 0.0,
    lng = 0.0,
    place = "place",
    address = "address"
)