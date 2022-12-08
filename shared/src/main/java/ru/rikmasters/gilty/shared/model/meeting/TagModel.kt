package ru.rikmasters.gilty.shared.model.meeting

import java.util.*

data class TagModel(

    val id: UUID,

    val title: String
)

val DemoTag = TagModel(
    UUID.randomUUID(),
    "Поход в кино"
)

val DemoTagList = listOf(DemoTag, DemoTag, DemoTag)
