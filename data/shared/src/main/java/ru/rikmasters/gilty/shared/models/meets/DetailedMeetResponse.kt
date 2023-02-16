package ru.rikmasters.gilty.shared.models.meets

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.common.extentions.durationToString
import ru.rikmasters.gilty.shared.model.enumeration.*
import ru.rikmasters.gilty.shared.model.enumeration.GenderType.valueOf
import ru.rikmasters.gilty.shared.model.enumeration.PhotoType.PHOTO
import ru.rikmasters.gilty.shared.model.image.ThumbnailModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.RequirementModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.models.Category
import ru.rikmasters.gilty.shared.models.Location
import ru.rikmasters.gilty.shared.models.User

data class DetailedMeetResponse(
    val id: String,
    val userId: String,
    val organizer: User,
    val category: Category,
    val type: String,
    val condition: String,
    val isOnline: Boolean,
    val tags: List<Tag>,
    val description: String? = null,
    val datetime: String,
    val duration: Int,
    val isPrivate: Boolean,
    val withoutResponds: Boolean,
    val status: String,
    val membersMax: Int,
    val membersCount: Int,
    val members: List<User>,
    val requirements: List<DetailedRequirement>? = null,
    val hideAddress: Boolean? = null,
    val location: Location? = null,
    val place: String? = null,
    val address: String? = null,
    val memberState: String,
    val price: Int? = null,
) {
    
    fun map() = FullMeetingModel(
        id = id,
        userId = userId,
        organizer = organizer.map(),
        category = category.map(),
        type = MeetType.valueOf(type),
        condition = ConditionType.valueOf(condition),
        isOnline = isOnline,
        tags = tags.map { it.map() },
        description = description ?: "",
        datetime = LocalDateTime.of(datetime).toString(),
        duration = durationToString(duration),
        isPrivate = isPrivate,
        withoutResponds = withoutResponds,
        status = MeetStatusType.valueOf(status),
        membersMax = membersMax,
        membersCount = membersCount,
        members = members.map { it.map() },
        requirements = requirements?.let { req -> req.map { it.map() } },
        hideAddress = hideAddress,
        location = location?.map(),
        place = place,
        address = address,
        memberState = MemberStateType.valueOf(memberState),
        price = price
    )
}

data class DetailedRequirement(
    val number: Int? = null,
    val gender: String? = null,
    val orientation: Orientation? = null,
    val age: Age? = null,
) {
    
    fun map() = RequirementModel(
        gender?.let { valueOf(it) },
        age?.min,
        age?.max,
        orientation?.map()
    )
}

data class Age(
    val min: Int,
    val max: Int,
)

data class Orientation(
    val id: String,
    val name: String,
) {
    
    fun map() = OrientationModel(id, name)
}

data class Avatar(
    val id: String,
    val albumId: String,
    val ownerId: String,
    val type: String,
    val thumbnail: Thumbnail,
    val mimetype: String,
    val filesize: Int,
    val url: String,
    val width: Int,
    val height: Int,
    val hasAccess: Boolean,
) {
    
    fun map() = AvatarModel(
        id, albumId, ownerId,
        PHOTO, thumbnail.map(),
        mimetype, filesize, url,
        width, height, hasAccess
    )
}

data class Thumbnail(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    val filesize: Int,
    val mimetype: String,
) {
    
    fun map() = ThumbnailModel(
        id, url, width, height,
        filesize, mimetype
    )
}