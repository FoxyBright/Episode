package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.image.*
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
    val avatar: AvatarModel?,
    val thumbnail: ThumbnailModel?,
    val age: Int,
    val isAnonymous: Boolean,
    val isOnline: Boolean,
){
    fun mapToMember() = MemberModel(
        id, gender, username, emoji, avatar,
        thumbnail, age, isAnonymous, isOnline
    )
}

val DemoOrganizerModel = OrganizerModel(
    id = randomUUID().toString(),
    gender = FEMALE,
    username = "alina.loon",
    emoji = DemoEmojiModel,
    avatar = DemoAvatarModel,
    thumbnail = DemoThumbnailModel,
    age = 20,
    isAnonymous = false,
    isOnline = false,
)
