package ru.rikmasters.gilty.shared.model.profile

import ru.rikmasters.gilty.shared.model.enumeration.SexType
import ru.rikmasters.gilty.shared.model.enumeration.SexType.FEMALE
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.meeting.OrganizerModel

data class ProfileModel(
    
    val id: String,
    
    val phone: String,
    
    val username: String,
    
    val emoji: EmojiModel,
    
    val gender: SexType,
    
    val orientation: OrientationModel,
    
    val age: Int,
    
    val aboutMe: String,
    
    val avatar: AvatarModel,
    
    val rating: RatingModel,
    
    val isComplete: Boolean
) {
    
    @Suppress("unused")
    fun mapToOrganizerModel() = OrganizerModel(
        id, username, emoji,
        avatar, age, gender
    )
    
    @Suppress("unused")
    fun mapToMemberModel() = MemberModel(
        id, username, emoji,
        avatar, age, gender
    )
}

val DemoProfileModel = ProfileModel(
    "0",
    "+7 910 524-12-12",
    "alina.loon",
    DemoEmojiModel,
    FEMALE,
    DemoOrientationModel,
    27,
    "Instagram @cristi",
    DemoAvatarModel,
    DemoRatingModel,
    true
)

@Suppress("unused")
fun MemberModel.toProfileModel(): MemberModel {
    return MemberModel(
        id, username, emoji,
        avatar, age, gender
    )
}