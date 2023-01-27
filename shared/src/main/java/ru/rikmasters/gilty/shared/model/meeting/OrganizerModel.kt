package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.image.DemoEmojiModel
import ru.rikmasters.gilty.shared.image.EmojiModel
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import java.util.UUID.randomUUID

data class OrganizerModel(
    val id: String,
    val gender: GenderType,
    val username: String,
    val emoji: EmojiModel,
    val avatar: AvatarModel,
    val age: Int,
    val isAnonymous: Boolean,
    val isOnline: Boolean,
)

val DemoOrganizerModel = OrganizerModel(
    id = randomUUID().toString(),
    gender = FEMALE,
    username = "alina.loon",
    emoji = DemoEmojiModel,
    avatar = DemoAvatarModel,
    age = 20,
    isAnonymous = false,
    isOnline = false,
)
