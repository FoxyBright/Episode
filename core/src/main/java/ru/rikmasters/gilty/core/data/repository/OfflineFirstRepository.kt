package ru.rikmasters.gilty.core.data.repository

import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.data.source.WebSource

abstract class OfflineFirstRepository<W: WebSource, D: DbSource>(
    
    protected open val webSource: W,
    
    override val primarySource: D,
): Repository<DbSource>(primarySource)