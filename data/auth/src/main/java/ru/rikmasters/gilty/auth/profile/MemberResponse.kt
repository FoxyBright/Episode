package ru.rikmasters.gilty.auth.profile

import ru.rikmasters.gilty.auth.meetings.Thumbnail
import ru.rikmasters.gilty.shared.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.valueOf
import ru.rikmasters.gilty.shared.model.meeting.MemberModel

data class MemberResponse(
    val id: String,
    val gender: String,
    val username: String,
    val emojiType: String? = null,
    val thumbnail: Thumbnail,
    val age: Int,
) {
    
    fun map(): MemberModel = MemberModel(
        id = id,
        gender = valueOf(gender),
        username = username,
        emoji = getEmoji(emojiType.toString()),
        thumbnail.map().map(),
        thumbnail.map(),
        age = age,
        isAnonymous = false,
        isOnline = false
    )
}