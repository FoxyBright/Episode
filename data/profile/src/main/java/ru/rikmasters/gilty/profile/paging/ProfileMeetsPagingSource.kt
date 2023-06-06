package ru.rikmasters.gilty.profile.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.profile.models.MeetingsType
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.models.meets.Meeting

class ProfileMeetsPagingSource(
    private val webSource: ProfileWebSource,
    private val type: MeetingsType,
): PagingSource<Int, MeetingModel>() {
    
    override fun getRefreshKey(state: PagingState<Int, MeetingModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page =
            state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
    
    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, MeetingModel> {
        val page: Int = params.key ?: 1
        return try {
            webSource.getUserMeets(
                page = page,
                perPage = params.loadSize,
                type = type
            ).on(
                success = { it },
                loading = { emptyList<Meeting>() to 0 },
                error = { emptyList<Meeting>() to 0 }
            ).let { meets ->
                Page(
                    data = meets.first.map { it.map() },
                    prevKey = if(page == 1)
                        null else meets.second - 1,
                    nextKey = if(meets.first.size < params.loadSize)
                        null else meets.second + 1
                )
            }
        } catch(e: Exception) {
            Error(e)
        }
    }
}
