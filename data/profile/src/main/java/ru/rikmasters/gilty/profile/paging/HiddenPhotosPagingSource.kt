package ru.rikmasters.gilty.profile.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.data.source.find
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.models.Profile

class HiddenPhotosPagingSource(
    private val webSource: ProfileWebSource,
    private val localSource: DbSource
) : PagingSource<Int, AvatarModel>() {
    override fun getRefreshKey(state: PagingState<Int, AvatarModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AvatarModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        return try {
            val profile = localSource.find<Profile>()

            val photos = webSource.getFilesPaging(
                page = page,
                perPage = loadSize,
                albumId = profile?.albumPrivate?.id ?: ""
            )
            Log.d("TEST","photos ${photos?.first}")
            val nextKey = if ((photos?.first?.size ?: 0) < loadSize) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(photos?.first?.map { it.map() } ?: emptyList(), prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
