package ru.rikmasters.gilty.notification.paginator

import ru.rikmasters.gilty.shared.wrapper.ResponseWrapper.Paginator

interface PagingManager<M: Any> {
    
    suspend fun getPage(
        page: Int, perPage: Int,
    ): Pair<List<M>, Paginator>
}