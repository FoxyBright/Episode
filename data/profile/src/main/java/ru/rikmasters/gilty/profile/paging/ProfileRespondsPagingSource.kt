package ru.rikmasters.gilty.profile.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.shared.model.notification.RespondModel

class ProfileRespondsPagingSource(
    private val webSource: ProfileWebSource,
    private val meetId: String
) : PagingSource<Int, RespondModel>() {

    override fun getRefreshKey(state: PagingState<Int, RespondModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RespondModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        return try {
            val responds = webSource.getMeetResponds(
                page = page,
                perPage = loadSize,
                meetId = meetId
            )
            val nextKey = if ((responds?.first?.size ?: 0) < loadSize) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(responds?.first?.map { it.map() } ?: emptyList(), prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}