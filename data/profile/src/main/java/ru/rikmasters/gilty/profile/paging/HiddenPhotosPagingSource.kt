package ru.rikmasters.gilty.profile.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.data.source.find
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.models.Profile

class HiddenPhotosPagingSource(
    private val webSource: ProfileWebSource,
    private val localSource: DbSource,
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
                albumId = localSource.find<Profile>()
                    ?.albumPrivate?.id ?: ""
            ).on(
                success = { it },
                loading = { emptyList() },
                error = { emptyList() }
            ).let { photos ->
                Page(
                    data = photos.map { it.map() },
                    prevKey = if(page == 1)
                        null else page - 1,
                    nextKey = if((photos.size) < params.loadSize)
                        null else page + 1
                )
            }
        } catch(e: Exception) {
            Error(e)
        }
    }
}
