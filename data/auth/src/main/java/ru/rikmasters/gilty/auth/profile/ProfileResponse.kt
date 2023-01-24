package ru.rikmasters.gilty.auth.profile

import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.PhotoType.PHOTO
import ru.rikmasters.gilty.shared.model.profile.*

data class ProfileResponse(
    
    val id: String,
    val phone: String? = null,
    val username: String? = null,
    val gender: String? = null,
    val orientation: OrientationModel? = null,
    val age: Int? = null,
    val aboutMe: String? = null,
    val emoji_type: String? = null,
    val average: String? = null,
    val avatar: Image? = null,
    val subscriptionExpiredAt: String? = null,
    val thumbnail: Thumbnail? = null,
    val responds: Responds? = null,
    val albumPrivate: AlbumPrivate? = null,
    val countWatchers: Int? = null,
    val countWatching: Int? = null,
    val isWatching: Boolean? = null,
    val unblockAt: String? = null,
    val isCompleted: Boolean? = null,
    val is_online: Boolean? = null,
    val is_anonymous: Boolean? = null,
    val status: String? = null,
) {
    
    fun map() = ProfileModel(
        id = id,
        phone = phone,
        username = username,
        gender = try {
            GenderType.valueOf(gender.toString())
        } catch(_: Exception) {
            GenderType.MALE
        },
        orientation = orientation,
        age = age ?: 0,
        aboutMe = aboutMe,
        rating = RatingModel(
            average = average ?: "0.0",
            emoji = getEmoji(emoji_type.toString())
        ),
        avatar = getImage(avatar?.url.toString(), avatar?.albumId),
        thumbnail = getImage(thumbnail?.url.toString()),
        isComplete = isCompleted == true,
        subscriptionExpiredAt = subscriptionExpiredAt,
        respondsCount = responds?.count,
        respondsImage = getImage(responds?.thumbnail?.url),
        hidden = getImage(albumPrivate?.preview?.url, albumPrivate?.preview?.albumId),
        count_watchers = countWatchers,
        count_watching = countWatching,
        is_watching = isWatching,
        unblock_at = unblockAt,
        is_completed = isCompleted,
        is_online = is_online,
        is_anonymous = is_anonymous,
        status = status
    )
    
    private fun getImage(
        image: String?,
        album: String? = null,
    ) = AvatarModel(
        id = image ?: "",
        albumId = album ?: "",
        ownerId = id,
        type = PHOTO,
        mimeType = "photo/jpeg",
        (0), (0), (0), (0), (0),
        (null), (true)
    )
}

data class Image(
    val id: String,
    val albumId: String,
    val thumbnail: Thumbnail,
    val url: String,
)

data class Responds(
    val count: Int? = null,
    val thumbnail: Thumbnail? = null,
)

data class Thumbnail(
    val id: String,
    val url: String,
)

data class AlbumPrivate(
    val id: String,
    val preview: Image? = null,
)