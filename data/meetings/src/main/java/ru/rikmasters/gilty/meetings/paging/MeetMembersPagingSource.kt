package ru.rikmasters.gilty.meetings.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import ru.rikmasters.gilty.meetings.MeetingWebSource
import ru.rikmasters.gilty.shared.model.meeting.UserModel

class MeetMembersPagingSource(
    private val webSource: MeetingWebSource,
    private val excludeMe: Int,
    private val meet: String,
): PagingSource<Int, UserModel>() {
    
    override fun getRefreshKey(
        state: PagingState<Int, UserModel>,
    ): Int? {
        val anchorPosition = state.anchorPosition
            ?: return null
        val page =
            state.closestPageToPosition(anchorPosition)
                ?: return null
        return page.prevKey?.plus(1)
            ?: page.nextKey?.minus(1)
    }
    
    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, UserModel> {
        val page: Int = params.key ?: 1
        return try {
            webSource.getMeetMembers(
                meet = meet,
                page = page,
                perPage = params.loadSize,
                excludeMe = excludeMe
            ).on(
                success = { it },
                loading = { emptyList() },
                error = { emptyList() }
            ).let { members ->
                Page(
                    data = members.map { it.map() },
                    prevKey = if(page == 1)
                        null else page - 1,
                    nextKey = if((members.size) < params.loadSize)
                        null else page + 1
                )
            }
        } catch(e: Exception) {
            Error(e)
        }
    }
}
