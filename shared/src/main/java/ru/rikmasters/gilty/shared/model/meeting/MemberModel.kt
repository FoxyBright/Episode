package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.image.*
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.MALE
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import java.util.UUID.randomUUID

data class MemberModel(
    val id: String,
    val gender: GenderType,
    val username: String,
    val emoji: EmojiModel,
    val avatar: AvatarModel?,
    val thumbnail: ThumbnailModel?,
    val age: Int,
    val isAnonymous: Boolean,
    val isOnline: Boolean,
) {
    
    fun mapToOrganizer() = OrganizerModel(
        id, gender, username, emoji, avatar,
        thumbnail, age, isAnonymous, isOnline
    )
}

val DemoMemberModel = MemberModel(
    id = randomUUID().toString(),
    gender = FEMALE,
    username = "cristina",
    emoji = DemoEmojiModel,
    avatar = DemoAvatarModel,
    thumbnail = DemoThumbnailModel,
    age = 23,
    isAnonymous = false,
    isOnline = false,
)

val DemoMemberModelTwo = MemberModel(
    id = "2",
    gender = MALE,
    username = "gosha",
    emoji = DemoEmojiModel,
    avatar = DemoAvatarModel,
    thumbnail = DemoThumbnailModel,
    age = 26,
    isAnonymous = true,
    isOnline = true,
)

val DemoMemberModelList = listOf(
    DemoMemberModel,
    DemoMemberModelTwo,
    DemoMemberModel,
    DemoMemberModelTwo,
)