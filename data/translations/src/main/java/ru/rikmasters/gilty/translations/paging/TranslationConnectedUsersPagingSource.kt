package ru.rikmasters.gilty.translations.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.translations.TranslationWebSource

class TranslationConnectedUsersPagingSource(
    private val webSource: TranslationWebSource,
    private val translationId: String
) : PagingSource<Int, TranslationMessageAuthor>() {

    override fun getRefreshKey(state: PagingState<Int, TranslationMessageAuthor>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TranslationMessageAuthor> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        return try {
            val users = webSource.getMessages(
                page = page,
                perPage = loadSize,
                translationId = translationId
            )
            val nextKey = if ((users?.first?.size ?: 0) < loadSize) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(users?.first?.map { it.map() } ?: emptyList(), prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}