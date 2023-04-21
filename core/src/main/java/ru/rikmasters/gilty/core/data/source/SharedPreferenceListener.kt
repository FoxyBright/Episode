package ru.rikmasters.gilty.core.data.source

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener

class SharedPrefListener<T: Any>(
    private val sharedKey: String,
    private var defValue: T,
    private val block: (T) -> Unit,
): OnSharedPreferenceChangeListener {
    
    companion object {
        
        fun <T: Any> Context.listenPreference(
            key: String, defValue: T,
            sharedPrefName: String = "sharedPref",
            block: (T) -> Unit,
        ) {
            @Suppress("UNUSED_VARIABLE")
            val preferenceManager = this.getSharedPreferences(
                sharedPrefName, MODE_PRIVATE
            )?.registerOnSharedPreferenceChangeListener(
                SharedPrefListener(key, defValue, block)
            )
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?,
    ) {
        sharedPreferences?.let { pref ->
            if(pref.contains(key)) block(
                when(defValue) {
                    is Int -> pref.getInt(
                        sharedKey, defValue as Int
                    )
                    
                    is String -> pref.getString(
                        sharedKey,
                        defValue as String
                    )
                    
                    is Boolean -> pref.getBoolean(
                        sharedKey,
                        defValue as Boolean
                    )
                    
                    is Float -> pref.getFloat(
                        sharedKey, defValue as Float
                    )
                    
                    is Long -> pref.getLong(
                        sharedKey, defValue as Long
                    )
                    
                    else -> throw IllegalArgumentException(
                        "недопустимый тип ${defValue::class.java}. " +
                                "Доступные к извлечению: " +
                                "Int, String, Boolean, Float, Long"
                    )
                } as T
            )
        }
    }
}