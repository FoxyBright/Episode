package ru.rikmasters.gilty.shared.model.profile

import java.util.UUID.randomUUID

data class OrientationModel(
    val id: String,
    val name: String,
)

val DemoOrientationModel = OrientationModel(
    id = randomUUID().toString(),
    name = "HETERO"
)