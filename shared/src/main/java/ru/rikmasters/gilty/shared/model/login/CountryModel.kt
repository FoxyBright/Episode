package ru.rikmasters.gilty.shared.model.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.json.JSONArray
import org.json.JSONObject
import ru.rikmasters.gilty.shared.R

data class Country(
    val country: String,
    val code: String,
    val flag: Int
)

@Composable
fun Countries(): List<Country> {
    val context = LocalContext.current
    val countryList = arrayListOf<Country>()
    val jsonArray = JSONArray(
        stringResource(R.string.countries)
    )
    for(i in 0 until jsonArray.length()) {
        val country = JSONObject(jsonArray[i].toString())
        val name = country.get("name").toString()
        val code = country.get("dial_code").toString()
        val flag = country.get("code").toString()
        val img = context.resources.getIdentifier(
            "drawable/" + flag.lowercase()
                .lowercase(), null,
            context.packageName
        )
        countryList.add(
            Country(
                name, code, if(img == 0) R.drawable.ic_image_empty else img
            )
        )
    }
    return countryList
}

val DemoCountry = Country("Россия", "+7", R.drawable.ru)