package ru.rikmasters.gilty.translations.webrtc.utils

import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
    when (val value = this[it])
    {
        is JSONArray ->
        {
            val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
            JSONObject(map).toMap().values.toList()
        }
        is JSONObject -> value.toMap()
        JSONObject.NULL -> null
        else            -> value
    }
}

inline fun <reified T> List<*>.asListOfType(): List<T>? =
    if (all { it is T })
        @Suppress("UNCHECKED_CAST")
        this as List<T> else
        null

inline fun <reified T> Map<*,*>.asMapOfKeysType(): Map<T,*>? =
    if (all { it.key is T })
        @Suppress("UNCHECKED_CAST")
        this as Map<T,*> else
        null