package ru.rikmasters.gilty.translations.datasource.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.shared.model.translations.TranslationMessageModel
import ru.rikmasters.gilty.shared.models.translations.TranslationMessageDTO
import ru.rikmasters.gilty.shared.wrapper.paginateState
import ru.rikmasters.gilty.translations.datasource.remote.TranslationWebSource

class TranslationMessagesPagingSource(
    private val webSource: TranslationWebSource,
    private val translationId: String
) : PagingSource<Int, TranslationMessageModel>() {

    override fun getRefreshKey(state: PagingState<Int, TranslationMessageModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TranslationMessageModel> {
        val page: Int = params.key ?: 1
        val loadSize = params.loadSize

        return paginateState(
            block = {
                val messages = webSource.getMessages(
                    translationId = translationId,
                    page = page,
                    perPage = loadSize
                ).map(TranslationMessageDTO::map)
                Log.d("WebSock","MEssages $messages")
                messages
            },
            loadSize = loadSize,
            page = page
        )
    }
}