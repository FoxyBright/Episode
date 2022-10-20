package ru.rikmasters.gilty.presentation.model.profile

data class EmojiModel(

    val type: String,

    val path: String

)

val DemoEmojiModel = EmojiModel(

    type = "type",
    path = "https://serverspace.ru/wp-content/uploads/2019/06/backup-i-snapshot.png"

)

val AllEmojiList = listOf(
    DemoEmojiModel,
    DemoEmojiModel,
    DemoEmojiModel,
    DemoEmojiModel,
    DemoEmojiModel,
    DemoEmojiModel,
    DemoEmojiModel,
    DemoEmojiModel
)