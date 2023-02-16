package ru.rikmasters.gilty.shared.models.chats

import ru.rikmasters.gilty.shared.model.chat.MemberMessageModel
import ru.rikmasters.gilty.shared.models.User

data class MemberMessage(
    val author: User,
    val text: String,
    val attachments: List<ChatAttachment>? = null,
    val is_author: Boolean,
) {
    
    fun map() = MemberMessageModel(
        author.map(), text,
        attachments?.map { it.map() },
        is_author
    )
}
