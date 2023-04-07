package ru.rikmasters.gilty.meetings.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.meetings.MeetingWebSource
import ru.rikmasters.gilty.shared.model.meeting.UserModel

class MeetMembersPagingSource(
    private val webSource: MeetingWebSource,
    private val excludeMe: Int,
    private val meet: String
) : PagingSource<Int, UserModel>() {
    override fun getRefreshKey(state: PagingState<Int, UserModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        return try {
            val members = webSource.getMeetMembers(
                meet = meet,
                page = page,
                perPage = loadSize,
                excludeMe = excludeMe
            )
            val nextKey = if ((members?.first?.size ?: 0) < loadSize) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(members?.first?.map { it.map() } ?: emptyList(), prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
