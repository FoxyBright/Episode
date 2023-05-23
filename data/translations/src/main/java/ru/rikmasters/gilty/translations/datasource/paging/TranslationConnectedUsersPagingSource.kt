package ru.rikmasters.gilty.translations.datasource.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.models.FullUserDTO
import ru.rikmasters.gilty.shared.wrapper.paginateState
import ru.rikmasters.gilty.translations.datasource.remote.TranslationWebSource

class TranslationConnectedUsersPagingSource(
    private val webSource: TranslationWebSource,
    private val translationId: String,
    private val query: String?,
) : PagingSource<Int, FullUserModel>() {

    override fun getRefreshKey(state: PagingState<Int, FullUserModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FullUserModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        query?.let {
            if (it.isBlank()) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        }

        return paginateState(
            block = {
                webSource.getConnectedUsers(
                    translationId = translationId,
                    page = page,
                    perPage = loadSize,
                    query = query,
                ).map(FullUserDTO::map)
            },
            loadSize = loadSize,
            page = page,
        )
    }
}