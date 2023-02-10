package ru.rikmasters.gilty.shared.model.meeting

data class LocationModel(
    val hide: Boolean?,
    val lat: Int?,
    val lng: Int?,
    val country: String,
    val subject: String,
    val city: String,
    val place: String?,
    val address: String?,
)

@Suppress("unused")
val DemoLocationModel = LocationModel(
    hide = true,
    lat = 0,
    lng = 0,
    country = "RU",
    subject = "subject",
    city = "city",
    place = "place",
    address = "address"
)