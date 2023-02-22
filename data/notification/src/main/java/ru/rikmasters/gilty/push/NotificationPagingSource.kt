package ru.rikmasters.gilty.push

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

class NotificationPagingSource(
    
    private val manager: NotificationManager,
): PagingSource<Int, NotificationModel>() {
    
    override fun getRefreshKey(state: PagingState<Int, NotificationModel>): Int? {
        return state.anchorPosition?.let { position ->
            val page =
                state.closestPageToPosition(position)
            page?.prevKey?.minus(1) ?: page?.nextKey?.plus(1)
        }
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationModel> {
        return try {
            val page = params.key ?: 1
            val response = manager.getNotification(page)
            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = page + 1
            )
        } catch(e: Exception) {
            LoadResult.Error(e)
        }
    }
}