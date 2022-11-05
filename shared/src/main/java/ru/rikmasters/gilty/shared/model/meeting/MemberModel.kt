package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoEmojiModel
import ru.rikmasters.gilty.shared.model.profile.EmojiModel

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

val DemoMemberModelList = listOf(
    DemoMemberModel,
    DemoMemberModel,
    DemoMemberModel,
    DemoMemberModel,
)