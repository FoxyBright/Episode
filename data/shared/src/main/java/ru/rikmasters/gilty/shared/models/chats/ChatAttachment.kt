package ru.rikmasters.gilty.shared.models.chats

import ru.rikmasters.gilty.shared.model.chat.AttachmentType
import ru.rikmasters.gilty.shared.model.chat.ChatAttachmentModel
import ru.rikmasters.gilty.shared.models.meets.Avatar

data class ChatAttachment(
    val type: String,
    val file: Avatar,
) {
    
    fun map() = ChatAttachmentModel(
        AttachmentType.valueOf(type),
        file.map()
    )
}
