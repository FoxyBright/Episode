package ru.rikmasters.gilty.presentation.model.profile

import java.util.*

data class OrientationModel(

    val id: UUID,

    val name: String
)

val OrientationModelDemo = OrientationModel(

    UUID.randomUUID(),

    "гетеросексуал"
)