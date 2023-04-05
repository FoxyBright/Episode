package ru.rikmasters.gilty.chats.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.chats.models.chat.SortType
import ru.rikmasters.gilty.chats.source.web.ChatWebSource
import ru.rikmasters.gilty.shared.model.chat.ChatModel

class ChatListPagingSource(
    private val webSource: ChatWebSource,
    private val sortType: SortType
) : PagingSource<Int, ChatModel>() {
    override fun getRefreshKey(state: PagingState<Int, ChatModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        Log.d("TESSST","anchor $anchorPosition")
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        Log.d("TESSST", "page $page")
        Log.d("TESSST", "loadSize $loadSize")

        return try {
            val chats = webSource.getDialogs(
                page = page,
                perPage = loadSize,
                sortType = sortType
            )
            Log.d("GEWGG","chats ${chats.first.map { it.title }}")
            Log.d("TESSST", "chats paginator curPAge${chats.second.currentPage} listPAge${chats.second.list_page} perPGE${chats.second.perPage} offset${chats.second.offset} limit${chats.second.limit} size ${chats.first.size}")
            val nextKey = if (chats.first.size < loadSize) null else chats.second.currentPage + 1
            Log.d("TESSST", "nextKey $nextKey")
            val prevKey = if (page == 1) null else chats.second.currentPage - 1
            Log.d("TESSST", "prevKey $prevKey")
            LoadResult.Page(chats.first.map { it.map() }, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
