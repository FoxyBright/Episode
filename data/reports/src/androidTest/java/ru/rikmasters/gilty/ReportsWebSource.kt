package ru.rikmasters.gilty


import io.ktor.client.request.setBody
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.report.Report
import ru.rikmasters.gilty.shared.model.report.ReportObjectType
import ru.rikmasters.gilty.shared.model.report.ReportSubtype

class ReportsWebSource: KtorSource() {
    
    suspend fun sendReport(
        type: Report,
        subtype: ReportSubtype,
        description: String?,
        objectId: String,
        objectType: ReportObjectType,
    ) {
        post(
            "http:/$HOST$PREFIX_URL/reports"
        ) {
            setBody(
                ReportRequest(
                    type.name,
                    subtype.name,
                    description,
                    objectId,
                    objectType.name
                )
            )
        }
    }
}