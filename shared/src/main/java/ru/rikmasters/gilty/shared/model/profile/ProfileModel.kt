package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.image.DemoThumbnailModel
import ru.rikmasters.gilty.shared.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.image.ThumbnailModel
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.MALE
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import java.util.UUID.randomUUID

data class ProfileModel(
    val id: String,
    val phone: String?,
    val username: String?,
    val gender: GenderType,
    val orientation: OrientationModel?,
    val age: Int,
    val aboutMe: String?,
    val rating: RatingModel,
    val avatar: AvatarModel? = null,
    val thumbnail: ThumbnailModel,
    val isCompleted: Boolean,
    val subscriptionExpiredAt: String?,
    val respondsCount: Int?,
    val respondsImage: ThumbnailModel?,
    val hidden: AvatarModel?,
    val countWatchers: Int?,
    val countWatching: Int?,
    val isWatching: Boolean?,
    val unblockAt: String?,
    val isOnline: Boolean?,
    val isAnonymous: Boolean?,
    val status: String?,
) {
    
    companion object {
        
        val empty = ProfileModel(
            id = "", phone = null, username = null,
            gender = MALE,
            orientation = null,
            age = 0, aboutMe = null,
            rating = RatingModel(average = "0.0", getEmoji(icon = "")),
            avatar = DemoAvatarModel.copy(
                url = "", thumbnail = DemoThumbnailModel.copy(url = "")
            ),
            thumbnail = DemoThumbnailModel.copy(url = ""),
            isCompleted = true,
            subscriptionExpiredAt = null,
            respondsCount = null,
            respondsImage = null,
            hidden = null,
            countWatchers = 0,
            countWatching = 0,
            isWatching = null,
            unblockAt = null,
            isOnline = null,
            isAnonymous = null,
            status = null
        )
    }
    
    fun map() = UserModel(
        id = id,
        gender = gender,
        username = username.toString(),
        emoji = rating.emoji,
        avatar = avatar,
        thumbnail = thumbnail,
        age = age,
        isAnonymous = isAnonymous == true,
        isOnline = isOnline == true
    )
}

val DemoProfileModel = ProfileModel(
    id = randomUUID().toString(),
    phone = "+7 910 524-12-12",
    username = "alina.loon",
    gender = FEMALE,
    orientation = DemoOrientationModel,
    age = 27,
    aboutMe = "Instagram @cristi",
    rating = DemoRatingModel,
    avatar = DemoAvatarModel,
    thumbnail = DemoThumbnailModel,
    isCompleted = true,
    subscriptionExpiredAt = "",
    respondsCount = 6,
    respondsImage = DemoThumbnailModel,
    hidden = DemoAvatarModel,
    countWatchers = 10,
    countWatching = 1500,
    isWatching = true,
    unblockAt = "",
    isOnline = true,
    isAnonymous = true,
    status = ""
)