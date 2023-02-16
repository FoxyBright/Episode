package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.models.meets.Avatar
import ru.rikmasters.gilty.shared.models.meets.Thumbnail

data class User(
    val id: String? = null,
    val gender: String? = null,
    val username: String? = null,
    val emojiType: String? = null,
    val avatar: Avatar? = null,
    val thumbnail: Thumbnail? = null,
    val age: Int? = null,
    val isAnonymous: Boolean? = null,
    val isOnline: Boolean? = null,
    val meetingRating: Rating? = null,
) {
    
    fun map() = UserModel(
        id = id,
        gender = gender?.let { GenderType.valueOf(it) },
        username = username,
        emoji = emojiType?.let { getEmoji(it) },
        avatar = avatar?.map(),
        thumbnail = thumbnail?.map(),
        age = age,
        isAnonymous = isAnonymous,
        isOnline = isOnline,
        meetRating = meetingRating?.map()
    )
}