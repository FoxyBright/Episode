package ru.rikmasters.gilty.shared.model.profile

data class EmojiModel(

    val type: String,

    val path: String

)

val DemoEmojiModel = EmojiModel("type", "https://placekitten.com/1200/800")

val DemoEmojiList = listOf(DemoEmojiModel, DemoEmojiModel, DemoEmojiModel)