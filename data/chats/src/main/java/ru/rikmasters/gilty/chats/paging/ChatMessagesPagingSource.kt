package ru.rikmasters.gilty.chats.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.shared.model.chat.MessageModel
import java.lang.Exception

class ChatMessagesPagingSource(
    private val webSource: ChatWebSource,
    private val chatId: String
) : PagingSource<Int, MessageModel>() {
    override fun getRefreshKey(state: PagingState<Int, MessageModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize
        return try {
            val chats = webSource.getMessages(
                chatId = chatId,
                page = page,
                perPage = loadSize
            )
            val nextKey = if (chats.first.size < loadSize) null else chats.second.currentPage + 1
            val prevKey = if (page == 1) null else chats.second.currentPage - 1
            LoadResult.Page(chats.first.map { it.map() }, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}