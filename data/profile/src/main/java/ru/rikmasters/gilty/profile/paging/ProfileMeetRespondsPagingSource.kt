package ru.rikmasters.gilty.profile.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModel

class ProfileMeetRespondsPagingSource(
    private val webSource: ProfileWebSource,
    private val type: RespondType,
): PagingSource<Int, MeetWithRespondsModel>() {
    
    override fun getRefreshKey(state: PagingState<Int, MeetWithRespondsModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page =
            state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
    
    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, MeetWithRespondsModel> {
        val page: Int = params.key ?: 1
        return try {
            webSource.getResponds(
                page = page,
                perPage = params.loadSize,
                type = type
            ).on(
                success = { it },
                loading = { emptyList() },
                error = { emptyList() }
            ).let { responds ->
                Page(
                    data = responds.map { it.map() },
                    prevKey = if(page == 1)
                        null else page - 1,
                    nextKey = if(responds.size < params.loadSize)
                        null else page + 1
                )
            }
        } catch(e: Exception) {
            Error(e)
        }
    }
}