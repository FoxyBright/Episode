package ru.rikmasters.gilty.shared.model.meeting

data class CityModel(
    val id: Int,
    val name: String,
)

val DemoCityModel = CityModel(
    111, "Москва"
)