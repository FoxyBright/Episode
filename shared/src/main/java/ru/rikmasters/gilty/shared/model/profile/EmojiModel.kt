package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.R


data class EmojiModel(

    val type: String,

    val path: String

)

val DemoEmojiModel = EmojiModel("URL", "https://placekitten.com/1200/800")

val EmojiList = listOf(
    EmojiModel("D", "${R.drawable.ic_love}"),
    EmojiModel("D", "${R.drawable.ic_bad}"),
    EmojiModel("D", "${R.drawable.ic_cry}"),
    EmojiModel("D", "${R.drawable.ic_cuty}"),
    EmojiModel("D", "${R.drawable.ic_admiration}"),
    EmojiModel("D", "${R.drawable.ic_devil}"),
    EmojiModel("D", "${R.drawable.ic_drops}"),
    EmojiModel("D", "${R.drawable.ic_clown}"),
    EmojiModel("D", "${R.drawable.ic_broken_heart}"),
    EmojiModel("D", "${R.drawable.ic_fire}"),
    EmojiModel("D", "${R.drawable.ic_batterfly}"),
    EmojiModel("D", "${R.drawable.ic_money_love}"),
    EmojiModel("D", "${R.drawable.ic_sarcasm}"),
)