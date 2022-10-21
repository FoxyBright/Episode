package ru.rikmasters.gilty.presentation.model.profile

data class EmojiModel(

    val type: String,

    val path: String

)

val DemoEmojiModel = EmojiModel("type", "https://placekitten.com/1200/800")

val DemoEmojiList = listOf(DemoEmojiModel, DemoEmojiModel, DemoEmojiModel)