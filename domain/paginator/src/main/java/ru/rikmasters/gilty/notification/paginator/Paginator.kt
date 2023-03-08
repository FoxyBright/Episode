package ru.rikmasters.gilty.notification.paginator

import androidx.paging.PagingSource
import androidx.paging.PagingState

open class Paginator<T: Any, M: PagingManager<T>>(
    private val manager: M,
    private val subParams: Boolean = false,
): PagingSource<Int, T>() {
    
    override suspend fun load(
        params: LoadParams<Int>,
    ) = try {
        manager.getPage((params.key ?: 1), params.loadSize)
            .let { (list, paginator) ->
                LoadResult.Page(
                    list, (null), if(list.isNotEmpty())
                        paginator.currentPage + 1 else null
                )
            }
    } catch(e: Exception) {
        LoadResult.Error(e)
    }
    
    override fun getRefreshKey(
        state: PagingState<Int, T>,
    ) = state.anchorPosition
        ?.let { state.closestPageToPosition(it) }
        .let { it?.prevKey?.minus(1) ?: it?.nextKey?.plus(1) }
}