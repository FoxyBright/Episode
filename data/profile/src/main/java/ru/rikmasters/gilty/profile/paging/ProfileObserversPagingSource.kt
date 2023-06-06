package ru.rikmasters.gilty.profile.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType
import ru.rikmasters.gilty.shared.model.meeting.UserModel

class ProfileObserversPagingSource(
    private val webSource: ProfileWebSource,
    private val type: ObserversType,
    private val query: String,
): PagingSource<Int, UserModel>() {
    
    override fun getRefreshKey(state: PagingState<Int, UserModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page =
            state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
    
    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, UserModel> {
        val page: Int = params.key ?: 1
        return try {
            webSource.getObservers(
                query = query,
                page = page,
                perPage = params.loadSize,
                type = type
            ).on(
                success = { it },
                loading = { emptyList() },
                error = { emptyList() }
            ).let { users ->
                Page(
                    data = users.map { it.map() },
                    prevKey = if(page == 1)
                        null else page - 1,
                    nextKey = if((users.size) < params.loadSize)
                        null else page + 1
                )
            }
        } catch(e: Exception) {
            Error(e)
        }
    }
}
