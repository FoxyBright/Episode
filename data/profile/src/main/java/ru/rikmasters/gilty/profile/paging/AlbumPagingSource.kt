package ru.rikmasters.gilty.profile.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

class AlbumPagingSource(
    private val webSource: ProfileWebSource,
    private val albumId: String
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
            val albums = webSource.getFilesPaging(
                page = page,
                perPage = loadSize,
                albumId = albumId
            )
            val nextKey = if ((albums?.first?.size ?: 0) < loadSize) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(albums?.first?.map { it.map() } ?: emptyList(), prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}