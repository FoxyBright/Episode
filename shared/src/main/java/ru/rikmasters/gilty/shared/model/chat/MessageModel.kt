package ru.rikmasters.gilty.shared.model.chat

import ru.rikmasters.gilty.shared.model.enumeration.ChatNotificationType
import ru.rikmasters.gilty.shared.model.enumeration.MessageType
import ru.rikmasters.gilty.shared.model.enumeration.MessageType.MESSAGE
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import java.util.UUID

data class MessageModel(
    val id: String,
    val type: MessageType,
    val replied: MessageModel? = null,
    val notification: ChatNotificationModel? = null,
    val message: MemberMessageModel? = null,
    val otherRead: Boolean,
    val isRead: Boolean,
    val isDelivered: Boolean,
    val createdAt: String,
)

val DemoMessageModel = MessageModel(
    UUID.randomUUID().toString(),
    MESSAGE, (null), (null),
    DemoMemberMessageModel,
    (true), (true), (true),
    "2022-10-17T08:35:54.140Z"
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
