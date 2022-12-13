package ru.rikmasters.gilty.login

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


enum class Countries(
    val country: String,
    val code: String,
    val flag: String
) {
    
    // TODO Оставлю пока тут - чтобы не забыть сделать нормально флаги и коды стран
    CI("Кот-д’Ивуар", "+225", "\uD83C\uDDE8\uD83C\uDDEE")
}

@Composable
@Suppress("unused")
fun CountryText() {
    val con = Countries.CI
    Text("${con.country}  ${con.code}  ${con.flag}")
}



