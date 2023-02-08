package ru.rikmasters.gilty.shared.model.profile

data class OrientationModel(
    val id: String,
    val name: String,
)

val DemoOrientationModel = OrientationModel(
    id = "HETERO",
    name = "Гетеро"
)