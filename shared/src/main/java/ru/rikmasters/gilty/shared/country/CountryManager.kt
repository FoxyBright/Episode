package ru.rikmasters.gilty.shared.country

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.json.JSONObject
import org.koin.androidx.compose.get
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.common.Component
import java.security.InvalidParameterException
import java.util.Locale

@Immutable
data class Country(
    val name: String,
    val code: String,
    val phoneDial: String,
    val phoneMask: String
) {
    val flag: ImageBitmap @Composable get() {
        val manager = get<CountryManager>()
        return manager.flagForCountry(code)
    }
    val clearPhoneDial by lazy {
        phoneDial.replace(Regex("\\D"), "")
    }
}

val DemoCountry = Country(
    "Россия", "GU", "+7", "+7 (###) ###-##-##"
)

class CountryManager: Component {
    
    private val context by inject<Context>()
    
    val defaultCountry: Country by lazy {
        countryFrom(Locale.getDefault())
    }
    
    private fun countryFrom(locale: Locale) =
        masks[locale.country]!!.let {
            Country(
                Locale("", locale.country).displayName,
                locale.country,
                it.dial,
                it
            )
        }
    
    private fun countryFrom(code: String, mask: String) =
        Country(
            Locale("", code).displayName,
            code,
            mask.dial,
            mask
        )
    
    private val masks: Map<String, String> by lazy {
        val json = context.assets
            .open("country_phone_code_masks.json")
            .reader().use {
                JSONObject(it.readText())
            }
        
        mutableMapOf<String, String>().apply {
            json.keys().forEach {
                this[it] = json[it] as String
            }
        }
    }

    fun getCountries(): List<Country> {
        return masks.map {
            countryFrom(it.key, it.value)
        }
    }
    
    private val String.dial: String get() {
        val index = replace(" ", "")
            .indexOfAny(listOf("#", "-#", ")#", "(#"))
        return substring(0, index).let {
            if(it.contains("(") && !it.contains(")"))
                "$it)"
            else
                it
        }
    }
    
    companion object {
        const val FLAG_WIDTH = 32
        const val FLAG_HEIGHT = 22
    }
    
    private val flags = mutableMapOf<String, ImageBitmap>()
    
    fun flagForCountry(countryCode: String): ImageBitmap {
        
        flags[countryCode]?.let { return it }
        
        if(countryCode.length != 2) {
            throw InvalidParameterException("Country code is not valid. Supply a valid ISO two digit country code. Refer: https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2")
        }
        val ch = countryCode.uppercase(Locale.getDefault()).toCharArray()
        val asciiIndex = 64
        val firstLetterPosition = ch[0].code - asciiIndex
        val secondLetterPosition = ch[1].code - asciiIndex
        val flagImage = getImageFromAssetsFile()
        val flagForCountry = Bitmap.createBitmap(
            flagImage,
            firstLetterPosition * FLAG_WIDTH,
            secondLetterPosition * FLAG_HEIGHT,
            FLAG_WIDTH,
            FLAG_HEIGHT
        )
        return flagForCountry.asImageBitmap()
            .also { flags[countryCode] = it }
    }
    
    private var flagImageBitmap: Bitmap? = null
    
    private fun getImageFromAssetsFile(): Bitmap {
        flagImageBitmap?.let { return it }
        val bitmap = context.assets.open("flags.png").use {
            BitmapFactory.decodeStream(it)
        }
        flagImageBitmap = bitmap
        return bitmap
    }
}