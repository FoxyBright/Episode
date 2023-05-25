package ru.rikmasters.gilty.notification.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import ru.rikmasters.gilty.notification.NotificationWebSource
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

class NotificationListPagingSource(
    private val webSource: NotificationWebSource,
): PagingSource<Int, NotificationModel>() {
    
    override fun getRefreshKey(state: PagingState<Int, NotificationModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page =
            state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
    
    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, NotificationModel> {
        val page: Int = params.key ?: 1
        return try {
            webSource.getNotifications(
                page = page,
                perPage = params.loadSize
            ).on(
                success = { it },
                loading = { emptyList() },
                error = { emptyList() }
            ).let { list ->
                Page(
                    data = list.map { it.map() },
                    prevKey = if(page == 1)
                        null else page - 1,
                    nextKey = if(list.size < params.loadSize)
                        null else page + 1
                )
            }
        } catch(e: Exception) {
            Error(e)
        }
    }
}
