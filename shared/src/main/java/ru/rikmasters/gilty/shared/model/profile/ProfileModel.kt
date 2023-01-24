package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.FEMALE
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.MALE
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.meeting.OrganizerModel

data class ProfileModel(
    
    val id: String,
    
    val phone: String?,
    
    val username: String?,
    
    val gender: GenderType,
    
    val orientation: OrientationModel?,
    
    val age: Int,
    
    val aboutMe: String?,
    
    val rating: RatingModel,
    
    val avatar: ImageModel,
    
    val thumbnail: ImageModel,
    
    val isComplete: Boolean,
    
    val subscriptionExpiredAt: String?,
    
    val respondsCount: Int?,
    
    val respondsImage: ImageModel?,
    
    val hidden: ImageModel?,
    
    val count_watchers: Int?,
    
    val count_watching: Int?,
    
    val is_watching: Boolean?,
    
    val unblock_at: String?,
    
    val is_completed: Boolean?,
    
    val is_online: Boolean?,
    
    val is_anonymous: Boolean?,
    
    val status: String?,
) {
    
    fun mapToOrganizerModel() = OrganizerModel(
        id, username.toString(), rating.emoji,
        avatar.map(), age, gender
    )
    
    @Suppress("unused")
    fun mapToMemberModel() = MemberModel(
        id, username.toString(), rating.emoji,
        avatar.map(), age, gender
    )
}

val DemoProfileModel = ProfileModel(
    ("0"), ("+7 910 524-12-12"),
    ("alina.loon"),
    FEMALE,
    DemoOrientationModel, (27),
    ("Instagram @cristi"),
    DemoRatingModel,
    DemoAvatarModel,
    DemoAvatarModel,
    (true), (""), (6),
    DemoAvatarModel,
    DemoAvatarModel,
    (10), (1500), (true),
    (""), (true), (true),
    (true), ("")
)

private const val empty = ""
val DemoEmptyProfileModel = ProfileModel(
    empty, empty, empty, MALE,
    OrientationModel(empty, empty),
    (0), empty,
    RatingModel(empty, getEmoji(empty)),
    getDemoAvatarModel(empty),
    getDemoAvatarModel(empty),
    (false), empty, (0),
    getDemoAvatarModel(empty),
    getDemoAvatarModel(empty),
    (0), (0), (false), empty,
    (false), (false), (false), empty
)