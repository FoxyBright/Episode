package ru.rikmasters.gilty.push.model

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.model.enumeration.NotificationStatus
import ru.rikmasters.gilty.shared.model.enumeration.NotificationType
import ru.rikmasters.gilty.shared.model.enumeration.PhotoType
import ru.rikmasters.gilty.shared.model.notification.*
import ru.rikmasters.gilty.shared.model.profile.RatingModel
import ru.rikmasters.gilty.shared.models.response.meets.MeetingResponse
import ru.rikmasters.gilty.shared.models.response.meets.Organizer
import ru.rikmasters.gilty.shared.models.response.meets.Thumbnail

data class NotificationResponse(
    val id: String,
    val date: String,
    val type: String,
    val status: String,
    val parent: ParentNotificationResponse,
    val feedback: FeedBack? = null,
) {
    
    fun map() = NotificationModel(
        id, ("${of(date)}"),
        NotificationType.valueOf(type),
        NotificationStatus.valueOf(status),
        NotificationParentModel(
            parent.meeting?.map(),
            parent.file?.map(),
            parent.notification
        ),
        feedback?.map()
    )
}

data class FeedBack(
    val respond: RespondResponse? = null,
    val ratings: List<RatingResponse>? = null,
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

data class RatingResponse(
    val emojiType: String,
    val value: Double,
)

data class RespondResponse(
    val id: String,
    val author: Organizer,
    val comment: String,
    val albumId: String? = null,
    val photoAccess: Boolean,
) {
    
    fun map() = RespondModel(
        id, author.map(), comment,
        albumId ?: "", photoAccess
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
    val meeting: MeetingResponse? = null,
    val file: FileResponse? = null,
    val notification: ShortNotification? = null,
)