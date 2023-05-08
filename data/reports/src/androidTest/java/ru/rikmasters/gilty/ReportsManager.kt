package ru.rikmasters.gilty

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.shared.model.report.Report
import ru.rikmasters.gilty.shared.model.report.ReportObjectType
import ru.rikmasters.gilty.shared.model.report.ReportSubtype

class ReportsManager(
    private val web: ReportsWebSource,
) {
    
    @Suppress("unused")
    suspend fun sendReport(
        type: Report,
        subtype: ReportSubtype,
        description: String? = null,
        objectId: String,
        objectType: ReportObjectType,
    ) {
        withContext(IO) {
            web.sendReport(
                type, subtype, description,
                objectId, objectType
            )
        }
    }
}