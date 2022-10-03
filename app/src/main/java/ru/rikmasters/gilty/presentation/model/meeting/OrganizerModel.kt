package ru.rikmasters.gilty.presentation.model.meeting

import ru.rikmasters.gilty.presentation.model.profile.AvatarModel
import ru.rikmasters.gilty.presentation.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.presentation.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.presentation.model.profile.EmojiModel

data class OrganizerModel(

    val id: String,

    val username: String,

    val emoji: EmojiModel,

    val avatar: AvatarModel,

    val age: Int
)

val DemoOrganizerModel = OrganizerModel(
    "https://placekitten.com/400/800",
    "alina.loon",
    DemoEmojiModel,
    DemoAvatarModel,
    20
)
