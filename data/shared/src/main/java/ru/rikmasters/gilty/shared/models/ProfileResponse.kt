package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.model.profile.RatingModel
import ru.rikmasters.gilty.shared.models.meets.Avatar
import ru.rikmasters.gilty.shared.models.meets.Thumbnail

data class ProfileRequest(
    val username: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val orientationId: String? = null,
    val aboutMe: String? = null,
)

data class ProfileResponse(
    val id: String,
    val phone: String? = null,
    val username: String? = null,
    val gender: String? = null,
    val orientation: OrientationModel? = null,
    val age: Int? = null,
    val aboutMe: String? = null,
    val emojiType: String? = null,
    val average: String? = null,
    val avatar: Avatar? = null,
    val subscriptionExpiredAt: String? = null,
    val thumbnail: Thumbnail,
    val responds: Responds? = null,
    val albumPrivate: AlbumPrivate? = null,
    val countWatchers: Int? = null,
    val countWatching: Int? = null,
    val isWatching: Boolean? = null,
    val unblockAt: String? = null,
    val isCompleted: Boolean? = null,
    val isOnline: Boolean? = null,
    val isAnonymous: Boolean? = null,
    val status: String? = null,
) {
    
    fun map() = ProfileModel(
        id = id,
        phone = phone,
        username = username,
        gender = try {
            GenderType.valueOf(gender.toString())
        } catch(e: Exception) {
            GenderType.MALE
        },
        orientation = orientation,
        age = age ?: 0,
        aboutMe = aboutMe,
        rating = RatingModel(
            average = average ?: "0.0",
            emoji = getEmoji(emojiType.toString())
        ),
        avatar = avatar?.map(),
        thumbnail = thumbnail.map(),
        isCompleted = isCompleted == true,
        subscriptionExpiredAt = subscriptionExpiredAt,
        respondsCount = responds?.count,
        respondsImage = responds?.thumbnail?.map(),
        hidden = albumPrivate?.preview?.map(),
        countWatchers = countWatchers,
        countWatching = countWatching,
        isWatching = isWatching,
        unblockAt = unblockAt,
        isOnline = isOnline,
        isAnonymous = isAnonymous,
        status = status
    )
}

data class Responds(
    val count: Int? = null,
    val thumbnail: Thumbnail? = null,
)

data class AlbumPrivate(
    val id: String,
    val preview: Avatar? = null,
)