package ru.rikmasters.gilty.chats.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.chats.models.chat.SortType
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.shared.model.chat.ChatModel

class ChatListPagingSource(
    private val webSource: ChatWebSource,
    private val sortType: SortType,
    private val isArchiveOn: Boolean,
): PagingSource<Int, ChatModel>() {
    
    override fun getRefreshKey(state: PagingState<Int, ChatModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page =
            state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize
        
        return try {
            val chats = webSource.getDialogs(
                page = page,
                perPage = loadSize,
                sortType = sortType,
                isArchiveOn = isArchiveOn,
            ).on(
                success = { it },
                loading = { emptyList() },
                error = { emptyList() }
            )
            val nextKey = if(chats.size < loadSize) null else page + 1
            val prevKey = if(page == 1) null else page - 1
            LoadResult.Page(chats.map { it.map() }, prevKey, nextKey)
        } catch(e: Exception) {
            LoadResult.Error(e)
        }
    }
}
