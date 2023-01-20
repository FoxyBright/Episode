package ru.rikmasters.gilty.shared.model.meeting

import java.util.UUID

data class TagModel(
    
    val id: String,
    
    val title: String,
)

val DemoTag = TagModel(
    UUID.randomUUID().toString(),
    "Поход в кино"
)

val DemoTagList = listOf(DemoTag, DemoTag, DemoTag)
