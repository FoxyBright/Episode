package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.MALE
import ru.rikmasters.gilty.shared.model.profile.*

data class MemberModel(
    val id: String,
    val username: String,
    val emoji: EmojiModel,
    val avatar: AvatarModel,
    val age: Int,
    val gender: GenderType
)

val DemoMemberModel = MemberModel(
    id = "1",
    username = "cristina",
    emoji = DemoEmojiModel,
    avatar = DemoAvatarModel,
    age = 23,
    gender = FEMALE
)

val DemoMemberModelTwo = MemberModel(
    id = "2",
    username = "gosha",
    emoji = DemoEmojiModel,
    avatar = DemoAvatarModel,
    age = 26,
    gender = MALE
)

val DemoMemberModelList = listOf(
    DemoMemberModel,
    DemoMemberModelTwo,
    DemoMemberModel,
    DemoMemberModelTwo,
)