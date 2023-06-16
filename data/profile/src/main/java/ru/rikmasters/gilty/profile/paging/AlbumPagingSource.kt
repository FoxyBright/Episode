package ru.rikmasters.gilty.profile.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.models.Avatar

class AlbumPagingSource(
    private val webSource: ProfileWebSource,
    private val albumId: String,
): PagingSource<Int, AvatarModel>() {
    
    override fun getRefreshKey(state: PagingState<Int, AvatarModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page =
            state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
    
    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, AvatarModel> {
        val page: Int = params.key ?: 1
        return try {
            webSource.getFilesPaging(
                page = page,
                perPage = params.loadSize,
                albumId = albumId
            ).on(
                success = { it },
                loading = { emptyList<Avatar>() to 0 },
                error = { emptyList<Avatar>() to 0 }
            ).let { albums: Pair<List<Avatar>, Int> ->
                Page(
                    data = albums.first.map { it.map() },
                    prevKey = if(page == 1)
                        null else page - 1,
                    nextKey = if((albums.first.size) < params.loadSize)
                        null else page + 1
                )
            }
        } catch(e: Exception) {
            Error(e)
        }
    }
}