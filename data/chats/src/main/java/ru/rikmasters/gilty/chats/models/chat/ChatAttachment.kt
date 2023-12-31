package ru.rikmasters.gilty.chats.models.chat

import ru.rikmasters.gilty.shared.model.chat.AttachmentType
import ru.rikmasters.gilty.shared.model.chat.ChatAttachmentModel
import ru.rikmasters.gilty.shared.models.Avatar

data class ChatAttachment(
    val type: String,
    val file: Avatar? = null,
) {
    
    fun map() = ChatAttachmentModel(
        AttachmentType.valueOf(type),
        file?.map()
    )
}
