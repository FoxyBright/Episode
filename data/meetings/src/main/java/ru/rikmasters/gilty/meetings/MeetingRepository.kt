package ru.rikmasters.gilty.meetings

import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.models.meets.MeetFiltersRequest

class MeetingRepository(
    override val primarySource: DbSource,
    override val webSource: MeetingWebSource,
): OfflineFirstRepository<KtorSource, DbSource>(webSource, primarySource) {
    
    suspend fun getMeetings(
        filter: MeetFiltersRequest,
        page: Int,
        count: Boolean,
    ) = webSource
        .getMeetsList(
            page = page,
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
        )
}