package ru.rikmasters.gilty.auth.profile

import ru.rikmasters.gilty.shared.model.enumeration.PhotoType
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
    val about_me: Any?,
    val emoji_type: Any?,
    val average: Any?,
    val avatar: Image? = null,
    val subscription_expired_at: Any?,
    val thumbnail: Thumbnail? = null,
    val responds: Any?,
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
    
    fun map() = ProfileModel(
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
            avatar?.thumbnail?.url.toString(),
            "",
            id,
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
        RatingModel(average.toString(), getEmoji(emoji_type.toString())),
        is_completed as Boolean
    )
}

data class Image(
    val id: String,
    val thumbnail: Thumbnail
)

data class Thumbnail(
    val id: String,
    val url: String
)

data class AlbumPrivate(
    val id: String,
    val preview: Image? = null,
)