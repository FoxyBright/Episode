package ru.rikmasters.gilty.chats.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import ru.rikmasters.gilty.chats.models.message.Message
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.shared.model.chat.MessageModel

class ChatMessagesPagingSource(
    private val webSource: ChatWebSource,
    private val chatId: String,
): PagingSource<Int, MessageModel>() {
    
    override fun getRefreshKey(state: PagingState<Int, MessageModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page =
            state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageModel> {
        val page: Int = params.key ?: 1
        return try {
            webSource.getMessages(
                chatId = chatId,
                page = page,
                perPage = params.loadSize
            ).on(
                success = { it },
                loading = { emptyList<Message>() to 0 },
                error = { emptyList<Message>() to 0 }
            ).let { chats ->
                Page(
                    data = chats.first.map { it.map() },
                    prevKey = if(page == 1)
                        null else chats.second - 1,
                    nextKey = if(chats.first.size < params.loadSize)
                        null else chats.second + 1
                )
            }
        } catch(e: Exception) {
            Error(e)
        }
    }
}