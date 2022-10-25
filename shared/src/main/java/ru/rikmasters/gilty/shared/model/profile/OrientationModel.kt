package ru.rikmasters.gilty.shared.model.profile

import java.util.*

data class OrientationModel(

    val id: UUID,

    val name: String
)

val DemoOrientationModel = OrientationModel(

    UUID.randomUUID(),

    "гетеросексуал"
)