package ru.rikmasters.gilty.chats.paging

import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.notification.paginator.Paginator
import ru.rikmasters.gilty.shared.model.chat.ChatModel

// источник пагинации для чатов
class ChatPagingSource(
    manager: ChatManager,
): Paginator<ChatModel, ChatManager>(manager)