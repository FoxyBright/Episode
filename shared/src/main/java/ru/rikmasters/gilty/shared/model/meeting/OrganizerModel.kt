package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel

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
