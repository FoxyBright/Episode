package ru.rikmasters.gilty.chats.paging.messages

import ru.rikmasters.gilty.meetings.mapper

internal fun <T: Any?> objToJSON(obj: T): String =
    obj?.let { mapper.writeValueAsString(obj) } ?: ""

internal inline fun <reified T: Any> objFromJSON(
    json: String,
): T? = if(json.isNotBlank())
    mapper.readValue(json, T::class.java)
else null