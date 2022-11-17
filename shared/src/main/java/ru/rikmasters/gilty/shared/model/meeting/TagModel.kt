package ru.rikmasters.gilty.shared.model.meeting

import java.util.UUID

data class TagModel(

    val id: UUID,

    val title: String
)

val DemoTag = TagModel(
    UUID.randomUUID(),
    "Какой-то тег"
)

val DemoTagList = listOf(DemoTag, DemoTag, DemoTag)
