package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.model.enumeration.NotificationStatus
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.enumeration.PhotoType
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.model.notification.*
import ru.rikmasters.gilty.shared.model.profile.RatingModel
import ru.rikmasters.gilty.shared.models.meets.Meeting

data class Notification(
    val id: String,
    val date: String,
    val type: String,
    val status: String,
    val parent: ParentNotificationResponse,
    val feedback: FeedBack? = null,
): DomainEntity {
    
    fun map() = NotificationModel(
        id, ("${of(date)}"),
        NotificationType.valueOf(type),
        NotificationStatus.valueOf(status),
        NotificationParentModel(
            parent.meeting?.map(),
            parent.file?.map(),
            parent.notification,
            parent.user?.map()
        ),
        feedback?.map()
    )
    
    override fun primaryKey() = id
}

data class FeedBack(
    val respond: RespondResponse? = null,
    val ratings: List<Rating>? = null,
) {
    
    fun map() = FeedBackModel(
        respond?.map(),
        ratings?.map {
            RatingModel(
                it.value.toString(),
                getEmoji(it.emojiType)
            )
        }
    )
}

data class RespondResponse(
    val id: String,
    val author: User,
    val comment: String? = null,
    val albumId: String? = null,
    val photoAccess: Boolean? = null,
) {
    
    fun map() = RespondModel(
        id, author.map(), comment.toString(),
        albumId ?: "", photoAccess = false
    )
}

data class FileResponse(
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
    val resolutionX: Int? = null,
    val resolutionY: Int? = null,
    val playtimeSeconds: Int? = null,
    val hasAccess: Boolean,
) {
    
    fun map() = FileModel(
        id, albumId, ownerId,
        PhotoType.valueOf(type),
        thumbnail.map(), mimetype,
        filesize, url, width, height,
        hasAccess, (resolutionX ?: 0),
        (resolutionY ?: 0),
        (playtimeSeconds ?: 0)
    )
}

data class ParentNotificationResponse(
    val meeting: Meeting? = null,
    val file: FileResponse? = null,
    val notification: ShortNotification? = null,
    val user: User? = null,
)