package ru.rikmasters.gilty.shared.common

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf

@Composable
fun <T: Any> pagingPreview(
    list: List<T>,
) = flowOf(
    PagingData.from(list)
).collectAsLazyPagingItems()