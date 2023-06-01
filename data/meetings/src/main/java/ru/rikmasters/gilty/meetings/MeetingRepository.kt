package ru.rikmasters.gilty.meetings

import androidx.paging.Pager
import androidx.paging.PagingConfig
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.meetings.paging.MeetingsPagingSource
import ru.rikmasters.gilty.shared.models.meets.MeetFiltersRequest

class MeetingRepository(
    override val primarySource: DbSource,
    override val webSource: MeetingWebSource,
): OfflineFirstRepository<KtorSource, DbSource>(webSource, primarySource) {

    // подгрузка уведомлений по страницам
    fun getMeetings(
        filter: MeetFiltersRequest,
        count: Boolean,
    ) = Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                MeetingsPagingSource(
                    webSource = webSource,
                    filter,
                    count
                )
            }
        ).flow
}