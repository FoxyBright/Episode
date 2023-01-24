package ru.rikmasters.gilty.auth.profile

import ru.rikmasters.gilty.shared.model.enumeration.PhotoType.PHOTO
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.getEmoji

data class MemberResponse(
    val id: String,
    val gender: String?,
    val username: String?,
    val emoji_type: String?,
    val thumbnail: Thumbnail?,
    val age: Int?
) {
    
    fun map(): MemberModel = MemberModel(
        id, username.toString(),
        getEmoji(emoji_type.toString()),
        AvatarModel(
            thumbnail?.url.toString(), "", "",
            PHOTO, "image/jpg",
            0, 0, 0, 0, 0,
            null, true
        ), age ?: 0, GenderType.valueOf(gender.toString())
    )
}