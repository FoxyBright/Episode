package ru.rikmasters.gilty.profile.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModel

class ProfileMeetRespondsPagingSource(
    private val webSource: ProfileWebSource,
    private val type: RespondType
) : PagingSource<Int, MeetWithRespondsModel>() {

    override fun getRefreshKey(state: PagingState<Int, MeetWithRespondsModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        Log.d("TEST","anchwegweor $anchorPosition")
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MeetWithRespondsModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        return try {
            val responds = webSource.getResponds(
                page = page,
                perPage = loadSize,
                type = type
            )
            val nextKey = if ((responds?.first?.size ?: 0) < loadSize) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(responds?.first?.map { it.map() } ?: emptyList(), prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}