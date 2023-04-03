package ru.rikmasters.gilty.notification.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.notification.NotificationWebSource
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import java.lang.Exception

class NotificationListPagingSource(
    private val webSource: NotificationWebSource
) : PagingSource<Int, NotificationModel>() {
    override fun getRefreshKey(state: PagingState<Int, NotificationModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        Log.d("TESSST","anchor $anchorPosition")
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        Log.d("TESSST", "page $page")
        Log.d("TESSST", "loadSize $loadSize")

        return try {
            val notifications = webSource.getNotifications(
                page = page,
                perPage = loadSize
            )
            Log.d("TESSST", "chats paginator curPAge${notifications.second.currentPage} listPAge${notifications.second.list_page} perPGE${notifications.second.perPage} offset${notifications.second.offset} limit${notifications.second.limit} size ${notifications.first.size}")
            val nextKey = if (notifications.first.size < loadSize) null else notifications.second.currentPage + 1
            Log.d("TESSST", "nextKey $nextKey")
            val prevKey = if (page == 1) null else notifications.second.currentPage - 1
            Log.d("TESSST", "prevKey $prevKey")
            LoadResult.Page(notifications.first.map { it.map() }, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
