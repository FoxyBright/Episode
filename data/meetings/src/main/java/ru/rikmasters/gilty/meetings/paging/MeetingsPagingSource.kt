package ru.rikmasters.gilty.meetings.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.rikmasters.gilty.meetings.MeetingWebSource
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.models.meets.MeetFiltersRequest

class MeetingsPagingSource(
    private val webSource: MeetingWebSource,
    private val filter: MeetFiltersRequest,
    private val count: Boolean,
) : PagingSource<Int, MeetingModel>() {

    override fun getRefreshKey(
        state: PagingState<Int, MeetingModel>,
    ): Int? {
        val anchorPosition = state.anchorPosition
            ?: return null
        val page =
            state.closestPageToPosition(anchorPosition)
                ?: return null
        return page.prevKey?.plus(1)
            ?: page.nextKey?.minus(1)
    }

    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, MeetingModel> {
        val page: Int = params.key ?: 1
        return try {
            webSource.getMeetsList(
                count = count,
                group = filter.group,
                categories = filter.categories?.map { it.id },
                tags = filter.tags?.map { it.title },
                radius = filter.radius,
                lat = filter.lat,
                lng = filter.lng,
                meetTypes = filter.meetTypes?.map { it.name },
                onlyOnline = filter.onlyOnline?.compareTo(false),
                conditions = filter.conditions?.map { it.name },
                genders = filter.genders?.map { it.name },
                dates = filter.dates,
                time = filter.time,
                city = filter.city?.id
            ).on(
                success = { it },
                loading = { emptyList() },
                error = { emptyList() }
            ).let { nots ->
                LoadResult.Page(
                    data = nots,
                    prevKey = if (page == 1)
                        null else page - 1,
                    nextKey = if ((nots.size) < params.loadSize)
                        null else page + 1
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
