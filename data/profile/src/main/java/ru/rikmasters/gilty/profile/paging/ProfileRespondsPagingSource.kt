package ru.rikmasters.gilty.profile.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.shared.model.notification.RespondModel

class ProfileRespondsPagingSource(
    private val webSource: ProfileWebSource,
    private val meetId: String,
): PagingSource<Int, RespondModel>() {
    
    override fun getRefreshKey(state: PagingState<Int, RespondModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page =
            state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
    
    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, RespondModel> {
        val page: Int = params.key ?: 1
        return try {
            webSource.getMeetResponds(
                page = page,
                perPage = params.loadSize,
                meetId = meetId
            ).on(
                success = { it },
                loading = { emptyList() },
                error = { emptyList() }
            ).let { list ->
                Page(
                    data = list.map { it.map() },
                    prevKey = if(page == 1) null else page - 1,
                    nextKey = if(list.size < params.loadSize)
                        null else page + 1
                )
            }
        } catch(e: Exception) {
            LoadResult.Error(e)
        }
    }
}