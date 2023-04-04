package ru.rikmasters.gilty.profile.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.profile.models.MeetingsType
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import java.lang.Exception

class ProfileMeetsPagingSource(
    private val webSource: ProfileWebSource,
    private val type: MeetingsType
) : PagingSource<Int, MeetingModel>() {

    override fun getRefreshKey(state: PagingState<Int, MeetingModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MeetingModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        return try {
            val meets = webSource.getUserMeets(
                page = page,
                perPage = loadSize,
                type = type
            )
            val nextKey = if (meets.size < loadSize) null else page + 1
            Log.d("TEST", "RESPONSE -> $meets")
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(meets.map { it.map() }, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
