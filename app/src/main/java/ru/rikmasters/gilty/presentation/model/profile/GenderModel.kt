package ru.rikmasters.gilty.presentation.model.profile

data class GenderModel(

    val id: String,

    val name: String
)

val DemoGenderModel = GenderModel(
    "id",
    "Гендер"
)