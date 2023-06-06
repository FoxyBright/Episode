package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.model.profile.RatingModel
import java.util.UUID.randomUUID

data class ProfileRequest(
    val username: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val orientationId: String? = null,
    val aboutMe: String? = null,
)

data class Profile(
    val id: String,
    val phone: String? = null,
    val username: String? = null,
    val gender: String? = null,
    val orientation: OrientationModel? = null,
    val age: Int? = null,
    val aboutMe: String? = null,
    val emojiType: String? = null,
    val average: Double? = null,
    val avatar: Avatar? = null,
    val subscriptionExpiredAt: String? = null,
    val thumbnail: Thumbnail? = null,
    val responds: Responds? = null,
    val albumPrivate: Album? = null,
    val countWatchers: Int? = null,
    val countWatching: Int? = null,
    val isWatching: Boolean? = null,
    val unblockAt: String? = null,
    val isCompleted: Boolean? = null,
    val isOnline: Boolean? = null,
    val isAnonymous: Boolean? = null,
    val status: String? = null,
): DomainEntity {
    
    constructor(): this(
        id = randomUUID().toString(),
        phone = (""),
        username = (""),
        gender = (""),
        orientation = OrientationModel(),
        age = (-1),
        aboutMe = (""),
        emojiType = (""),
        average = (0.0),
        avatar = Avatar(),
        subscriptionExpiredAt = (null),
        thumbnail = Thumbnail()
    )
    
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
            average = average?.let { "$it" } ?: "0.0",
            emoji = getEmoji(emojiType.toString())
        ),
        avatar = avatar?.map(),
        thumbnail = thumbnail?.map(),
        isCompleted = isCompleted ?: false,
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
        status = status,
        hiddenAccess = albumPrivate?.hasAccess
            ?: false
    )
    
    override fun primaryKey() = id
}

data class Responds(
    val count: Int? = null,
    val thumbnail: Thumbnail? = null,
)