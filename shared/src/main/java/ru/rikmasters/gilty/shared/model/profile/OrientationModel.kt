package ru.rikmasters.gilty.shared.model.profile

data class OrientationModel(
    val id: String,
    val name: String,
) {
    
    constructor(): this(
        id = "HETERO",
        name = "Гетеро"
    )
}

val DemoOrientationModel = OrientationModel()