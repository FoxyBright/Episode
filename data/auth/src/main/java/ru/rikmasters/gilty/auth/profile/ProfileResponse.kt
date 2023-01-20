package ru.rikmasters.gilty.auth.profile

import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.PhotoType
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.enumeration.SexType
import ru.rikmasters.gilty.shared.model.profile.*
import java.lang.Exception

data class ProfileResponse(
    
    val id: String,
    val phone: String?,
    val username: String?,
    val gender: Any?,
    val orientation: Any?,
    val age: Int?,
    val about_me: String?,
    val emoji_type: String?,
    val average: Any?,
    val avatar: Image? = null,
    val subscription_expired_at: Any?,
    val thumbnail: Thumbnail? = null,
    val responds: Responds?,
    val album_private: AlbumPrivate? = null,
    val count_watchers: Int?,
    val count_watching: Int?,
    val is_watching: Any?,
    val unblock_at: Any?,
    val is_completed: Any?,
    val is_online: Any?,
    val is_anonymous: Any?,
    val status: Any?
) {
    
    @Suppress("unused")
    fun mapToProfileState(
        type: ProfileType,
        enabled: Boolean
    ) = ProfileState(
        name = "${username}, $age",
        profilePhoto = avatar?.url,
        hiddenPhoto = album_private?.preview?.url.toString(),
        description = about_me ?: "",
        rating = average.toString(),
        observers = count_watchers ?: 0,
        observed = count_watching ?: 0,
        emoji = getEmoji(emoji_type.toString()),
        profileType = type,
        enabled = enabled
    )
    @Suppress("unused")
    fun mapToProfileModel() = ProfileModel(
        id,
        phone.toString(),
        username.toString(),
        getEmoji(emoji_type.toString()),
        try {
            SexType.valueOf(gender.toString())
        } catch(_: Exception) {
            SexType.MALE
        },
        OrientationModel("", "HETERO"),
        age ?: 0,
        about_me.toString(),
        AvatarModel(
            avatar?.url.toString(),
            "", id,
            PhotoType.PHOTO,
            "photo/jpeg",
            0,
            0,
            0,
            0,
            0,
            null,
            true
        ),
        RatingModel(
            average.toString(),
            getEmoji(emoji_type.toString())
        ), is_completed as Boolean
    )
}

data class Image(
    val id: String,
    val thumbnail: Thumbnail,
    val url: String
)

data class Responds(
    val count: Int? = null,
    val thumbnail: Thumbnail? = null
)

data class Thumbnail(
    val id: String,
    val url: String
)

data class AlbumPrivate(
    val id: String,
    val preview: Image? = null,
)