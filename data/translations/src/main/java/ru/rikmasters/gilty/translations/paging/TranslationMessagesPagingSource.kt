package ru.rikmasters.gilty.translations.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.translations.datasource.remote.TranslationWebSource
import ru.rikmasters.gilty.shared.models.translations.TranslationMessageDTO

class TranslationMessagesPagingSource(
    private val webSource: TranslationWebSource,
    private val translationId: String
) : PagingSource<Int, TranslationMessageDTO>() {

    override fun getRefreshKey(state: PagingState<Int, TranslationMessageDTO>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TranslationMessageDTO> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        return try {
            val messages = webSource.getMessages(
                page = page,
                perPage = loadSize,
                translationId = translationId
            )
            val nextKey = if ((messages?.first?.size ?: 0) < loadSize) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(messages?.first?.map { it.map() } ?: emptyList(), prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}