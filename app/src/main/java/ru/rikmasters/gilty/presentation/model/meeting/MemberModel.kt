package ru.rikmasters.gilty.presentation.model.meeting

import ru.rikmasters.gilty.presentation.model.profile.AvatarModel
import ru.rikmasters.gilty.presentation.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.presentation.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.presentation.model.profile.EmojiModel

data class MemberModel(

    val id: String,

    val username: String,

    val emoji: EmojiModel,

    val avatar: AvatarModel,

    val age: Int
)

val DemoMemberModel = MemberModel(

    id = "1",
    username = "cristina",
    emoji = DemoEmojiModel,
    avatar = DemoAvatarModel,
    age = 23
)
