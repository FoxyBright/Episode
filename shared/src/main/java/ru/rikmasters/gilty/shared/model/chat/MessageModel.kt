package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.common.extentions.NOW_DATE
import ru.rikmasters.gilty.shared.model.enumeration.ChatNotificationType
import ru.rikmasters.gilty.shared.model.enumeration.MessageType
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.MESSAGE
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import java.util.UUID.randomUUID

data class MessageModel(
    val id: String,
    val type: MessageType,
    val replied: MessageModel? = null,
    val notification: ChatNotificationModel? = null,
    val message: MemberMessageModel? = null,
    val otherRead: Boolean,
    val isRead: Boolean,
    val createdAt: String,
) {
    
    constructor(): this(
        id = randomUUID().toString(),
        type = MESSAGE,
        replied = null,
        notification = null,
        message = null,
        otherRead = false,
        isRead = false,
        createdAt = NOW_DATE
    )
}

val DemoMessageModel = MessageModel(
    id = randomUUID().toString(),
    type = MESSAGE,
    replied = null,
    notification = null,
    message = DemoMemberMessageModel,
    otherRead = true,
    isRead = true,
    createdAt = "2022-10-17T08:35:54.140Z"
)

val DemoLongMessageModel = DemoMessageModel.copy(
    message = DemoMemberMessageModel.copy(
        text = "МЫ РАСТЕМ КАК КАКТУСЫ " +
                "МЫ РАСТЕМ КАК КАКТУСЫ " +
                "МЫ РАСТЕМ КАК КАКТУСЫ " +
                "МЫ РАСТЕМ КАК КАКТУСЫ " +
                "МЫ РАСТЕМ КАК КАКТУСЫ " +
                "МЫ РАСТЕМ КАК КАКТУСЫ " +
                "МЫ РАСТЕМ КАК КАКТУСЫ " +
                "МЫ РАСТЕМ КАК КАКТУСЫ "
    )
)

val DemoImageMessageModel = DemoMessageModel.copy(
    message = DemoMemberMessageModel.copy(
        attachments = listOf(
            DemoChatAttachmentModel.copy(
                file = DemoAvatarModel
            )
        )
    )
)

val DemoMessageNotificationModel = DemoMessageModel.copy(
    notification = DemoChatNotificationModel, message = null
)

val DemoMessageModelList = listOf(
    DemoMessageNotificationModel.copy(
        notification = DemoChatNotificationModel.copy(
            type = ChatNotificationType.CHAT_CREATED
        )
    ),
    DemoMessageModel,
    DemoMessageNotificationModel.copy(
        notification = DemoChatNotificationModel.copy(
            type = ChatNotificationType.MEMBER_JOIN
        )
    ),
    DemoMessageNotificationModel.copy(
        notification = DemoChatNotificationModel.copy(
            type = ChatNotificationType.MEMBER_LEAVE
        )
    ),
    DemoMessageNotificationModel.copy(
        notification = DemoChatNotificationModel.copy(
            type = ChatNotificationType.TRANSLATION_START_5
        )
    ),
    DemoMessageNotificationModel.copy(
        notification = DemoChatNotificationModel.copy(
            type = ChatNotificationType.TRANSLATION_START_30
        )
    ),
)
