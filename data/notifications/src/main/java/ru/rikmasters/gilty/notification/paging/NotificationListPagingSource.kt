package ru.rikmasters.gilty.notification.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.notification.NotificationWebSource
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

class NotificationListPagingSource(
    private val webSource: NotificationWebSource
) : PagingSource<Int, NotificationModel>() {
    override fun getRefreshKey(state: PagingState<Int, NotificationModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        return try {
            val notifications = webSource.getNotifications(
                page = page,
                perPage = loadSize
            )
            Log.d("TESTNOT","notifications ${notifications.second.total}")
            val nextKey = if (notifications.first.size < loadSize) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(notifications.first.map { it.map() }, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
