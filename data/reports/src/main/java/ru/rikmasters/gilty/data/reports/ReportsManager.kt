package ru.rikmasters.gilty.data.reports

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.shared.model.report.ReportModel

class ReportsManager(private val web: ReportsWebSource) {
    
    suspend fun sendReport(report: ReportModel) = withContext(IO) {
        web.sendReport(
            ReportRequest(
                type = report.type?.name,
                subtype = report.subtype?.name,
                description = report.description?.let {
                    it.ifBlank { null }
                }, objectId = report.objectId,
                objectType = report.objectType?.name
            )
        )
    }
}