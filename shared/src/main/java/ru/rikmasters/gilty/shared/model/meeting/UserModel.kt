package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.MALE
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.image.*
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.RatingModel
import java.util.UUID.randomUUID

data class UserModel(
    val id: String?,
    val gender: GenderType?,
    val username: String?,
    val emoji: EmojiModel?,
    val group: UserGroupTypeModel?,
    val avatar: AvatarModel?,
    val thumbnail: ThumbnailModel?,
    val age: Int?,
    val isAnonymous: Boolean?,
    val isOnline: Boolean?,
    val meetRating: RatingModel? = null,
) {
    
    constructor(): this(
        (null), (null), (null),
        (null), (null), (null), (null),
        (null), (null), (null),
        (null),
    )
}

val DemoUserModel = UserModel(
    id = "userID",
    gender = FEMALE,
    username = "cristina",
    group = UserGroupTypeModel.DEFAULT,
    emoji = DemoEmojiModel,
    avatar = DemoAvatarModel,
    thumbnail = DemoThumbnailModel,
    age = 23,
    isAnonymous = false,
    isOnline = false
)

val DemoUserModelTwo = UserModel(
    id = randomUUID().toString(),
    gender = MALE,
    username = "gosha",
    emoji = DemoEmojiModel,
    group = UserGroupTypeModel.TEAM,
    avatar = DemoAvatarModel,
    thumbnail = DemoThumbnailModel,
    age = 40,
    isAnonymous = true,
    isOnline = true,
)

val DemoUserModelList = listOf(
    DemoUserModel,
    DemoUserModelTwo,
    DemoUserModel,
    DemoUserModelTwo,
)



/**
 * [FullUserModel] - модель пользователя с [group]
 */
data class FullUserModel(
    val id: String?,
    val gender: GenderType?,
    val username: String?,
    val group: UserGroupTypeModel?,
    val emoji: EmojiModel?,
    val avatar: AvatarModel?,
    val thumbnail: ThumbnailModel?,
    val age: Int?,
    val isAnonymous: Boolean?,
    val isOnline: Boolean?,
    val meetRating: RatingModel? = null,
)