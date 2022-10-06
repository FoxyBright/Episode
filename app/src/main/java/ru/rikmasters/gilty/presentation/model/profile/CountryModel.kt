package ru.rikmasters.gilty.presentation.model.profile

import ru.rikmasters.gilty.R

data class Country(
    val country: String,
    val code: String,
    val flag: Int
)

val DemoCountry by lazy { CountryList[0] }

val CountryList = listOf(
    Country("Россия", "+7", R.drawable.ru),
    Country("Австралия", "+61", R.drawable.au),
    Country("Армения", "+374", R.drawable.am),
    Country("США", "+1", R.drawable.us),
    Country("Германия", "+49", R.drawable.de),
    Country("Англия", "+44", R.drawable.gb),
    Country("Аргентина", "+54", R.drawable.ar)
)