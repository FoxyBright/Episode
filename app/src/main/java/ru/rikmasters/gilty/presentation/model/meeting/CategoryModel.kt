package ru.rikmasters.gilty.presentation.model.meeting

import ru.rikmasters.gilty.presentation.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.presentation.model.profile.EmojiModel

data class CategoryModel(

    val id: String,

    val name: String,

    val color: String,

    val emoji: EmojiModel
)

val DemoCategoryModel = CategoryModel(

    id = "test",
    name = "Название категории",
    color = "R.color.primary",
    emoji = DemoEmojiModel

)
