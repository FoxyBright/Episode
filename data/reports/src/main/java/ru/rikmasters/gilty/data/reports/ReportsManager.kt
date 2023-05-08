package ru.rikmasters.gilty.data.reports

import ru.rikmasters.gilty.shared.model.report.ReportModel

class ReportsManager(
    
    private val web: ReportsWebSource,
) {
    suspend fun sendReport(report: ReportModel) {
        web.sendReport(
            ReportRequest(
                report.type?.name,
                report.subtype?.name,
                report.description?.let {
                    it.ifBlank { null }
                }, report.objectId,
                report.objectType?.name
            )
        )
    }
}