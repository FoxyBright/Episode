package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.MALE
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.image.DemoThumbnailModel
import ru.rikmasters.gilty.shared.model.image.ThumbnailModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import java.util.UUID.randomUUID

data class ProfileModel(
    val id: String,
    val phone: String?,
    val username: String?,
    val gender: GenderType,
    val orientation: OrientationModel?,
    val group: UserGroupTypeModel?,
    val age: Int,
    val aboutMe: String?,
    val rating: RatingModel,
    val avatar: AvatarModel? = null,
    val thumbnail: ThumbnailModel? = null,
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
    val hiddenAccess: Boolean
) {
    
    constructor(): this(
        id = randomUUID().toString(),
        phone = null,
        username = null,
        gender = MALE,
        orientation = null,
        group = UserGroupTypeModel.DEFAULT,
        age = 0, aboutMe = null,
        rating = RatingModel(),
        avatar = AvatarModel(),
        thumbnail = ThumbnailModel(),
        isCompleted = true,
        subscriptionExpiredAt = null,
        respondsCount = null,
        respondsImage = null,
        hidden = null,
        countWatchers = null,
        countWatching = null,
        isWatching = null,
        unblockAt = null,
        isOnline = null,
        isAnonymous = null,
        status = null,
        hiddenAccess = false
    )
    
    fun map() = UserModel(
        id = id,
        gender = gender,
        username = username.toString(),
        group = group,
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
    group = UserGroupTypeModel.TEAM,
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
    status = "",
    hiddenAccess = false
)